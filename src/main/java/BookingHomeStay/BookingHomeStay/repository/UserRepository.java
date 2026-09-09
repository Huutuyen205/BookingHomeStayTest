package BookingHomeStay.BookingHomeStay.repository;

import BookingHomeStay.BookingHomeStay.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    // Dung cho trang /quan-tri/nguoi-dung: liet ke tat ca user co 1 vai tro cu the (theo ten Role, vd "ROLE_ADMIN")
    List<User> findByRoles_Name(String roleName);
}
