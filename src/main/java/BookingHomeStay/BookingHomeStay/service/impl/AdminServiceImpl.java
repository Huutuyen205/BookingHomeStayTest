package BookingHomeStay.BookingHomeStay.service.impl;

import BookingHomeStay.BookingHomeStay.dto.RoomForm;
import BookingHomeStay.BookingHomeStay.entity.*;
import BookingHomeStay.BookingHomeStay.exception.ResourceNotFoundException;
import BookingHomeStay.BookingHomeStay.repository.*;
import BookingHomeStay.BookingHomeStay.service.AdminService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final HostRepository hostRepository;
    private final HomestayRepository homestayRepository;
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final CollaboratorRepository collaboratorRepository;

    // Dem tong so nguoi dung / chu nha / homestay / don dang cho xu ly -> hien
    // thanh 4 the so lieu o dau trang Dashboard Admin.
    @Override
    public Map<String, Long> getDashboardStats() {
        Map<String, Long> stats = new LinkedHashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalHosts", hostRepository.count());
        stats.put("totalHomestays", homestayRepository.count());
        stats.put("pendingBookings", bookingRepository.countByStatus(BookingStatus.PENDING));
        return stats;
    }

    // Lay danh sach Chu nha co trang thai PENDING (dang cho Admin duyet).
    @Override
    public List<Host> getPendingHosts() {
        return hostRepository.findByStatus(HostStatus.PENDING);
    }

    // Chuyen trang thai 1 Chu nha sang APPROVED (duyet).
    @Override
    @Transactional
    public void approveHost(Long hostId) {
        Host host = hostRepository.findById(hostId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay host id=" + hostId));
        host.setStatus(HostStatus.APPROVED);
        hostRepository.save(host);
    }

    // Chuyen trang thai 1 Chu nha sang REJECTED (tu choi).
    @Override
    @Transactional
    public void rejectHost(Long hostId) {
        Host host = hostRepository.findById(hostId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay host id=" + hostId));
        host.setStatus(HostStatus.REJECTED);
        hostRepository.save(host);
    }

    // Gom so luong don dat phong theo tung thang (dung cho bieu do COT o Dashboard).
    @Override
    public Map<String, Long> getBookingCountByMonth() {
        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : bookingRepository.countBookingsGroupedByMonth()) {
            result.put((String) row[0], (Long) row[1]);
        }
        return result;
    }

    // Task 4: dem so don theo NGUON GOC (dat truc tiep vs qua Cong tac vien)
    // -> hien thanh bieu do TRON co chu thich o Dashboard Admin. Nguon goc nay
    // duoc gan tu dong luc tao don (xem BookingServiceImpl.createBooking()).
    @Override
    public Map<String, Long> getBookingCountBySource() {
        Map<String, Long> result = new LinkedHashMap<>();
        result.put("Đặt trực tiếp", bookingRepository.countBySource(BookingSource.DIRECT));
        result.put("Qua Cộng tác viên", bookingRepository.countBySource(BookingSource.COLLABORATOR));
        return result;
    }

    // Chia toan bo nguoi dung thanh 4 nhom theo vai tro (task 9), dung cho
    // trang "/quan-tri/nguoi-dung". LinkedHashMap de giu dung thu tu hien thi.
    @Override
    public Map<String, List<User>> getUsersGroupedByRole() {
        Map<String, List<User>> map = new LinkedHashMap<>();
        map.put("Quan tri vien", userRepository.findByRoles_Name("ROLE_ADMIN"));
        map.put("Chu nha (cho thue phong)", userRepository.findByRoles_Name("ROLE_HOST"));
        map.put("Cong tac vien / Sale", userRepository.findByRoles_Name("ROLE_COLLABORATOR"));
        map.put("Khach hang", userRepository.findByRoles_Name("ROLE_CUSTOMER"));
        return map;
    }

    // Lay TOAN BO phong trong he thong, khong loc theo Chu nha nao (task 1/2/5).
    @Override
    public List<Room> getAllRoomsForAdmin() {
        return roomRepository.findAll();
    }

    // Lay 1 phong theo id, khong kiem tra quyen so huu (chi Admin duoc goi ham nay).
    @Override
    public Room getRoomForAdmin(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay phong id=" + roomId));
    }

    // Cap nhat ten/gia/so khach/mo ta cho 1 phong BAT KY (task 5 - Admin duoc
    // sua gia cua bat ky phong nao, KHONG can kiem tra co phai chu phong khong -
    // day la diem KHAC BIET voi RoomServiceImpl.update() danh cho Chu nha).
    @Override
    @Transactional
    public void adminUpdateRoom(RoomForm form) {
        Room room = getRoomForAdmin(form.getId());
        room.setName(form.getName());
        room.setPricePerNight(form.getPricePerNight());
        room.setMaxGuests(form.getMaxGuests());
        room.setDescription(form.getDescription());
        roomRepository.save(room);
    }

    // Dao trang thai Con phong (ACTIVE) <-> Het phong (INACTIVE) cho 1 phong bat
    // ky, khong can la chu phong (task 17).
    @Override
    @Transactional
    public void adminToggleRoomStatus(Long roomId) {
        Room room = getRoomForAdmin(roomId);
        room.setStatus(room.getStatus() == RoomStatus.ACTIVE ? RoomStatus.INACTIVE : RoomStatus.ACTIVE);
        roomRepository.save(room);
    }

    // Danh sach dang ky lam Cong tac vien dang cho duyet (task 6).
    @Override
    public List<Collaborator> getPendingCollaborators() {
        return collaboratorRepository.findByStatus(CollaboratorStatus.PENDING);
    }

    // Xac nhan 1 nguoi dang ky la Cong tac vien HOP LE -> APPROVED. Sau buoc
    // nay, ma gioi thieu (maGioiThieu) cua ho moi co hieu luc de tinh hoa hong
    // khi khach dat phong bang ma do (xem BookingServiceImpl.createBooking()).
    @Override
    @Transactional
    public void approveCollaborator(Long collaboratorId) {
        Collaborator ctv = collaboratorRepository.findById(collaboratorId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay CTV id=" + collaboratorId));
        ctv.setStatus(CollaboratorStatus.APPROVED);
        collaboratorRepository.save(ctv);
    }

    // Tu choi 1 yeu cau dang ky Cong tac vien.
    @Override
    @Transactional
    public void rejectCollaborator(Long collaboratorId) {
        Collaborator ctv = collaboratorRepository.findById(collaboratorId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay CTV id=" + collaboratorId));
        ctv.setStatus(CollaboratorStatus.REJECTED);
        collaboratorRepository.save(ctv);
    }

    // Khoa/mo khoa 1 tai khoan nguoi dung. CHU Y: chi doi trang thai (BLOCKED
    // <-> ACTIVE) chu KHONG XOA tai khoan, de giu nguyen lich su don hang cu
    // (xoa cung se lam vo du lieu do khoa ngoai user_id trong bang bookings).
    @Override
    @Transactional
    public void toggleUserLock(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay user id=" + userId));
        user.setStatus(user.getStatus() == UserStatus.BLOCKED ? UserStatus.ACTIVE : UserStatus.BLOCKED);
        userRepository.save(user);
    }
}
