package BookingHomeStay.BookingHomeStay.repository;

import BookingHomeStay.BookingHomeStay.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    Optional<Promotion> findByCodeAndStatus(String code, String status);
}