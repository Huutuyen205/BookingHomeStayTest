package BookingHomeStay.BookingHomeStay.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Booking {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String bookingCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private BookingHomeStay.BookingHomeStay.entity.User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id")
    private BookingHomeStay.BookingHomeStay.entity.Promotion promotion;

    @Column(nullable = false, length = 100)
    private String customerName;

    @Column(nullable = false, length = 20)
    private String customerPhone;

    @Column(nullable = false, length = 100)
    private String customerEmail;

    @Column(nullable = false, precision = 13, scale = 2)
    private BigDecimal totalAmount;

    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal serviceFee = BigDecimal.ZERO;

    @Column(nullable = false, precision = 13, scale = 2)
    private BigDecimal finalAmount;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BookingHomeStay.BookingHomeStay.entity.BookingStatus status = BookingHomeStay.BookingHomeStay.entity.BookingStatus.PENDING;

    @Builder.Default
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingHomeStay.BookingHomeStay.entity.BookingDetail> details = new ArrayList<>();

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private BookingHomeStay.BookingHomeStay.entity.Payment payment;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    // ===== TASK 4: nguon goc don hang, dung ve bieu do tron o Dashboard Admin =====
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BookingHomeStay.BookingHomeStay.entity.BookingSource source = BookingHomeStay.BookingHomeStay.entity.BookingSource.DIRECT;

    // Ma gioi thieu CTV da dung khi dat don nay (null neu dat truc tiep).
    // Xem Collaborator.maGioiThieu - moi CTV co 1 ma rieng do Admin cap khi duyet.
    private String collaboratorCode;
}