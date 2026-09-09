package BookingHomeStay.BookingHomeStay.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "homestay_translations",
       uniqueConstraints = @UniqueConstraint(columnNames = {"homestay_id", "locale"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HomestayTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "homestay_id", nullable = false)
    private Homestay homestay;

    @Column(nullable = false, length = 5)
    private String locale; // vi, en, ja, ko, zh

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;
}