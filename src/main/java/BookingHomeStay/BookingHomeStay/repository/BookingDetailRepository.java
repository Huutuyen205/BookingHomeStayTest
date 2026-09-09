package BookingHomeStay.BookingHomeStay.repository;

import BookingHomeStay.BookingHomeStay.entity.BookingDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingDetailRepository extends JpaRepository<BookingDetail, Long> {

    /** Kiem tra trung lich - chi tinh cac booking dang giu cho phong */
    @Query("""
        SELECT bd FROM BookingDetail bd
        WHERE bd.room.id = :roomId
          AND bd.booking.status IN (BookingHomeStay.BookingHomeStay.entity.BookingStatus.PENDING,
                                     BookingHomeStay.BookingHomeStay.entity.BookingStatus.CONFIRMED,
                                     BookingHomeStay.BookingHomeStay.entity.BookingStatus.CHECKED_IN)
          AND bd.checkinDate < :checkOut
          AND bd.checkoutDate > :checkIn
        """)
    List<BookingDetail> findOverlapping(@Param("roomId") Long roomId,
                                         @Param("checkIn") LocalDate checkIn,
                                         @Param("checkOut") LocalDate checkOut);
}