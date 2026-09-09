package BookingHomeStay.BookingHomeStay.dto;

import BookingHomeStay.BookingHomeStay.entity.DiscountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PromotionForm {
    private Long id;
    @NotBlank private String code;
    @NotNull private DiscountType discountType;
    @NotNull @Positive private BigDecimal discountValue;
    private BigDecimal maxDiscountAmount;
    private BigDecimal minOrderAmount = BigDecimal.ZERO;
    @NotNull private LocalDate startDate;
    @NotNull private LocalDate endDate;
    private Integer usageLimit = 0;
}
