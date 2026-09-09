package BookingHomeStay.BookingHomeStay.service.impl;

import BookingHomeStay.BookingHomeStay.dto.RoomForm;
import BookingHomeStay.BookingHomeStay.entity.Homestay;
import BookingHomeStay.BookingHomeStay.entity.Room;
import BookingHomeStay.BookingHomeStay.entity.RoomStatus;
import BookingHomeStay.BookingHomeStay.exception.ResourceNotFoundException;
import BookingHomeStay.BookingHomeStay.repository.RoomRepository;
import BookingHomeStay.BookingHomeStay.service.HomestayService;
import BookingHomeStay.BookingHomeStay.service.RoomService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final HomestayService homestayService;

    @Override
    public List<Room> getRoomsOfHomestay(Long homestayId) {
        return roomRepository.findByHomestayId(homestayId);
    }

    @Override
    public Room getByIdForHost(Long roomId, Long hostUserId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay phong id=" + roomId));
        homestayService.getByIdForHost(room.getHomestay().getId(), hostUserId);
        return room;
    }

    @Override
    @Transactional
    public Room create(Long homestayId, RoomForm form, Long hostUserId) {
        Homestay homestay = homestayService.getByIdForHost(homestayId, hostUserId);
        Room room = Room.builder()
                .homestay(homestay)
                .name(form.getName())
                .pricePerNight(form.getPricePerNight())
                .maxGuests(form.getMaxGuests())
                .description(form.getDescription())
                .status(RoomStatus.ACTIVE)
                .build();
        return roomRepository.save(room);
    }

    @Override
    @Transactional
    public Room update(RoomForm form, Long hostUserId) {
        Room room = getByIdForHost(form.getId(), hostUserId);
        room.setName(form.getName());
        room.setPricePerNight(form.getPricePerNight());
        room.setMaxGuests(form.getMaxGuests());
        room.setDescription(form.getDescription());
        return roomRepository.save(room);
    }

    @Override
    @Transactional
    public void toggleActive(Long roomId, Long hostUserId) {
        Room room = getByIdForHost(roomId, hostUserId);
        room.setStatus(room.getStatus() == RoomStatus.ACTIVE ? RoomStatus.INACTIVE : RoomStatus.ACTIVE);
        roomRepository.save(room);
    }
}