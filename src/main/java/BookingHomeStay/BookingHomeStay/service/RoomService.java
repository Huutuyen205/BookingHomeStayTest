package BookingHomeStay.BookingHomeStay.service;

import BookingHomeStay.BookingHomeStay.dto.RoomForm;
import BookingHomeStay.BookingHomeStay.entity.Room;
import java.util.List;

public interface RoomService {
    List<Room> getRoomsOfHomestay(Long homestayId);
    Room getByIdForHost(Long roomId, Long hostUserId);
    Room create(Long homestayId, RoomForm form, Long hostUserId);
    Room update(RoomForm form, Long hostUserId);
    void toggleActive(Long roomId, Long hostUserId);
}