package BookingHomeStay.BookingHomeStay.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "promotions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Promotion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType;

    @Column(nullable = false, precision = 13, scale = 2)
    private BigDecimal discountValue;

    private BigDecimal maxDiscountAmount;

    @Builder.Default
    private BigDecimal minOrderAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Builder.Default
    private Integer usageLimit = 0;

    @Builder.Default
    private Integer usedCount = 0;

    @Builder.Default
    private String status = "ACTIVE";
}