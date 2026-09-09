package BookingHomeStay.BookingHomeStay.service;

import BookingHomeStay.BookingHomeStay.dto.BookingRequest;
import BookingHomeStay.BookingHomeStay.entity.Booking;
import java.util.List;

public interface BookingService {
    boolean isRoomAvailable(Long roomId, java.time.LocalDate checkIn, java.time.LocalDate checkOut);
    Booking createBooking(BookingRequest request, Long userId);
    List<Booking> getMyBookings(Long userId);
    void cancelBooking(Long bookingId, Long userId);
    List<Booking> getBookingsOfHost(Long hostUserId);
    void confirmBooking(Long bookingId, Long hostUserId);
    void rejectBooking(Long bookingId, Long hostUserId);
    void checkIn(Long bookingId, Long hostUserId);
    void checkOut(Long bookingId, Long hostUserId);
    List<Booking> getAllBookings();
}