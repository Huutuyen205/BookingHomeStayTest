package BookingHomeStay.BookingHomeStay.service;

import BookingHomeStay.BookingHomeStay.dto.RoomForm;
import BookingHomeStay.BookingHomeStay.entity.Collaborator;
import BookingHomeStay.BookingHomeStay.entity.Host;
import BookingHomeStay.BookingHomeStay.entity.Room;
import BookingHomeStay.BookingHomeStay.entity.User;
import java.util.List;
import java.util.Map;

public interface AdminService {
    Map<String, Long> getDashboardStats();
    List<Host> getPendingHosts();
    void approveHost(Long hostId);
    void rejectHost(Long hostId);

    // key = "yyyy-MM", value = so don dat phong trong thang do
    Map<String, Long> getBookingCountByMonth();

    // Task 4: du lieu ve bieu do TRON - nguon goc don hang.
    // key = nhan hien thi ("Đặt trực tiếp" / "Qua Cộng tác viên"), value = so don.
    Map<String, Long> getBookingCountBySource();

    // key = ten hien thi vai tro, value = danh sach user co vai tro do
    Map<String, List<User>> getUsersGroupedByRole();

    // Task 1/2/5: Admin CRUD toan bo phong trong he thong (khong phan biet cua host nao)
    List<Room> getAllRoomsForAdmin();
    Room getRoomForAdmin(Long roomId);
    void adminUpdateRoom(RoomForm form); // admin sua ten/gia/so khach/mo ta CUA BAT KY phong nao
    void adminToggleRoomStatus(Long roomId); // ACTIVE <-> INACTIVE, khong can la chu phong

    // Task 6: Admin xac nhan/tu choi dang ky lam Cong tac vien
    List<Collaborator> getPendingCollaborators();
    void approveCollaborator(Long collaboratorId);
    void rejectCollaborator(Long collaboratorId);

    // Task 1: Admin khoa/mo khoa tai khoan nguoi dung (khong xoa cung de giu lich su don hang)
    void toggleUserLock(Long userId);
}
