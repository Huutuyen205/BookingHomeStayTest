package BookingHomeStay.BookingHomeStay.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "rooms")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "homestay_id", nullable = false)
    private Homestay homestay;

    private String name;

    @Column(nullable = false, precision = 13, scale = 2)
    private BigDecimal pricePerNight;

    private Integer maxGuests;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    private RoomStatus status;
}