package BookingHomeStay.BookingHomeStay.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class BookingRequest {
    @NotNull private Long roomId;
    @NotNull @Future private LocalDate checkinDate;
    @NotNull private LocalDate checkoutDate;
    @Min(1) private Integer quantity = 1;
    @NotBlank private String customerName;
    @NotBlank private String customerPhone;
    @NotBlank @Email private String customerEmail;
    private String promotionCode;
    // Task 4: khach nhap ma gioi thieu cua Cong tac vien (neu co) - dung de gan
    // nguon goc don hang, phuc vu bieu do tron o Dashboard Admin.
    private String referralCode;
    private String paymentMethod = "CASH";
    private String note;
}