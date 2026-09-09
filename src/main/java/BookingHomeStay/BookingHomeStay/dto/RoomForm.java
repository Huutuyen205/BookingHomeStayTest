package BookingHomeStay.BookingHomeStay.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoomForm {
    private Long id;
    @NotBlank private String name;
    @NotNull @Positive private BigDecimal pricePerNight;
    @NotNull @Positive private Integer maxGuests;
    private String description;
}