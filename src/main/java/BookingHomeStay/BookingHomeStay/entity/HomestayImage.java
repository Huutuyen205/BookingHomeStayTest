package BookingHomeStay.BookingHomeStay.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "homestay_images")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HomestayImage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "homestay_id", nullable = false)
    private Homestay homestay;

    @Column(nullable = false, length = 500)
    private String imageUrl;
}