package BookingHomeStay.BookingHomeStay.repository;

import BookingHomeStay.BookingHomeStay.entity.Host;
import BookingHomeStay.BookingHomeStay.entity.HostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface HostRepository extends JpaRepository<Host, Long> {
    Optional<Host> findByUserId(Long userId);
    List<Host> findByStatus(HostStatus status);
}