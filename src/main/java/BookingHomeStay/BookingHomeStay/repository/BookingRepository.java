package BookingHomeStay.BookingHomeStay.repository;

import BookingHomeStay.BookingHomeStay.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Booking> findByDetails_Room_Homestay_Host_IdOrderByCreatedAtDesc(Long hostId);
    long countByStatus(BookingHomeStay.BookingHomeStay.entity.BookingStatus status);
    boolean existsByPromotionId(Long promotionId);
    long countBySource(BookingHomeStay.BookingHomeStay.entity.BookingSource source);

    // Gom so luong don dat phong theo thang (dung cho bieu do o Dashboard quan tri).
    // Tra ve moi hang la [ "yyyy-MM", so_luong ] de ve Chart.js.
    @Query("SELECT FUNCTION('DATE_FORMAT', b.createdAt, '%Y-%m'), COUNT(b) " +
           "FROM Booking b GROUP BY FUNCTION('DATE_FORMAT', b.createdAt, '%Y-%m') ORDER BY 1")
    List<Object[]> countBookingsGroupedByMonth();
}
