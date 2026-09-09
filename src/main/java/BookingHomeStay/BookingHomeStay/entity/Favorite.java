package BookingHomeStay.BookingHomeStay.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "favorites", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "homestay_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Favorite {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "homestay_id", nullable = false)
    private Homestay homestay;
}