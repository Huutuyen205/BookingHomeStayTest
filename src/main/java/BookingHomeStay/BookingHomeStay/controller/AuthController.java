package BookingHomeStay.BookingHomeStay.controller;

import BookingHomeStay.BookingHomeStay.dto.RegisterRequest;
import BookingHomeStay.BookingHomeStay.entity.OtpChannel;
import BookingHomeStay.BookingHomeStay.service.OtpService;
import BookingHomeStay.BookingHomeStay.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final OtpService otpService;

    // Goi bang AJAX tu nut "Gui ma" o trang dang ky (task 9).
    // dest = email hoac so dien thoai tuong ung voi kenh da chon.
    //
    // DA SUA LOI (09/2026): truoc day ham nay LUON tra ve message "Da gui ma
    // OTP..." bat ke otpService.sendOtp() co that su gui duoc hay khong (vi ham
    // do truoc la "void"). Nguoi dung chon kenh SMS/Zalo se thay "thanh cong"
    // nhung khong bao gio nhan duoc ma. Nay doc dung ket qua that va tra ve
    // them field "success" de giao dien to mau xanh/do chinh xac.
    @PostMapping("/dang-ky/gui-otp")
    @ResponseBody
    public Map<String, Object> guiOtp(@RequestParam String dest, @RequestParam OtpChannel kenh) {
        boolean daGuiThat = otpService.sendOtp(dest, kenh);
        if (daGuiThat) {
            return Map.of(
                    "success", true,
                    "message", "Đã gửi mã OTP qua " + kenh + " tới " + dest
            );
        }
        // Chua gui duoc qua kenh that (chua cau hinh SMTP/Zalo OA, hoac gui loi).
        // Van cho phep dang ky tiep tuc test duoc bang cach xem ma o console log
        // server (dong [OTP-EMAIL]/[OTP-SMS]/[OTP-ZALO]) - nhung PHAI noi that
        // voi nguoi dung la chua gui toi hop thu/dien thoai that cua ho.
        return Map.of(
                "success", false,
                "message", "Chưa gửi được mã qua " + kenh + " thật (kênh chưa được cấu hình hoặc gửi lỗi). "
                        + "Liên hệ quản trị viên hoặc xem log server để lấy mã demo."
        );
    }

    @GetMapping("/dang-nhap")
    public String loginPage() { return "xac-thuc/dang-nhap"; }

    @GetMapping("/dang-ky")
    public String registerPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "xac-thuc/dang-ky";
    }

    @PostMapping("/dang-ky")
    public String register(@Valid @ModelAttribute RegisterRequest registerRequest,
                            BindingResult result, Model model) {
        if (result.hasErrors()) return "xac-thuc/dang-ky";
        try {
            userService.register(registerRequest);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "xac-thuc/dang-ky";
        }
        return "redirect:/dang-nhap?registered=true";
    }

    @GetMapping("/khong-co-quyen")
    public String accessDenied() { return "loi/403"; }
}