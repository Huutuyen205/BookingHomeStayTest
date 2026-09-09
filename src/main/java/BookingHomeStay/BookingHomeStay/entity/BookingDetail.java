package BookingHomeStay.BookingHomeStay.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "booking_details")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BookingDetail {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false)
    private LocalDate checkinDate;

    @Column(nullable = false)
    private LocalDate checkoutDate;

    @Builder.Default
    private Integer quantity = 1;

    @Column(nullable = false, precision = 13, scale = 2)
    private BigDecimal pricePerNight;

    @Column(nullable = false, precision = 13, scale = 2)
    private BigDecimal subtotal;
}