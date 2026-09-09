package BookingHomeStay.BookingHomeStay.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class HomestayForm {
    private Long id;
    @NotBlank private String name;
    private String description;
    @NotBlank private String province;
    private String district;
    @NotBlank private String address;
    private Double latitude;
    private Double longitude;
}