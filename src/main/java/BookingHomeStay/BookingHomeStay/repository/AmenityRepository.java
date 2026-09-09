package BookingHomeStay.BookingHomeStay.repository;

import BookingHomeStay.BookingHomeStay.entity.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmenityRepository extends JpaRepository<Amenity, Long> {
}