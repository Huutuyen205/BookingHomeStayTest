package BookingHomeStay.BookingHomeStay.service.impl;

import BookingHomeStay.BookingHomeStay.dto.RegisterRequest;
import BookingHomeStay.BookingHomeStay.entity.*;
import BookingHomeStay.BookingHomeStay.repository.CollaboratorRepository;
import BookingHomeStay.BookingHomeStay.repository.HostRepository;
import BookingHomeStay.BookingHomeStay.repository.RoleRepository;
import BookingHomeStay.BookingHomeStay.repository.UserRepository;
import BookingHomeStay.BookingHomeStay.service.EmailService;
import BookingHomeStay.BookingHomeStay.service.OtpService;
import BookingHomeStay.BookingHomeStay.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final HostRepository hostRepository;
    private final CollaboratorRepository collaboratorRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final EmailService emailService;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Override
    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email da duoc su dung: " + request.getEmail());
        }

        // Xac thuc OTP truoc khi tao tai khoan (task 9). Dia chi nhan OTP la email hoac
        // so dien thoai tuy theo kenh nguoi dung chon o form dang ky.
        String otpDestination = request.getOtpChannel() == OtpChannel.EMAIL
                ? request.getEmail() : request.getPhone();
        if (request.getOtpCode() == null || !otpService.verifyOtp(otpDestination, request.getOtpCode())) {
            throw new IllegalArgumentException("Ma OTP khong dung hoac da het han, vui long thu lai.");
        }

        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName("ROLE_CUSTOMER").orElseThrow());
        if (request.isRegisterAsHost()) {
            roles.add(roleRepository.findByName("ROLE_HOST").orElseThrow());
        }
        if (request.isRegisterAsCollaborator()) {
            roles.add(roleRepository.findByName("ROLE_COLLABORATOR").orElseThrow());
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .roles(roles)
                .build();
        user = userRepository.save(user);

        if (request.isRegisterAsHost()) {
            Host host = Host.builder()
                    .user(user)
                    .businessName(request.getFullName())
                    .status(HostStatus.PENDING)
                    .build();
            hostRepository.save(host);
        }

        if (request.isRegisterAsCollaborator()) {
            Collaborator collaborator = Collaborator.builder()
                    .user(user)
                    .maGioiThieu("CTV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                    .soTaiKhoanNhanHoaHong(request.getBankAccountNumber())
                    .tenNganHang(request.getBankName())
                    .status(CollaboratorStatus.PENDING)
                    .build();
            collaboratorRepository.save(collaborator);
        }

        // Task 8: gui email thong bao dang ky thanh cong + link truy cap trang web.
        // Neu chua cau hinh SMTP that thi EmailService se tu log ra console, khong
        // lam gian doan qua trinh dang ky (xem EmailServiceImpl.java).
        emailService.send(
                user.getEmail(),
                "Đăng ký tài khoản BookingHomeStay thành công",
                "Xin chào " + user.getFullName() + ",\n\n"
                        + "Bạn đã đăng ký tài khoản BookingHomeStay thành công.\n"
                        + "Truy cập trang web tại: " + baseUrl + "/trang-chu\n"
                        + "Đăng nhập tại: " + baseUrl + "/dang-nhap\n\n"
                        + "Cảm ơn bạn đã sử dụng dịch vụ!"
        );

        return user;
    }
}
