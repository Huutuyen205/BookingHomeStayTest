package BookingHomeStay.BookingHomeStay.dto;

import BookingHomeStay.BookingHomeStay.entity.OtpChannel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank private String fullName;
    @NotBlank @Email private String email;
    @NotBlank @Size(min = 6) private String password;
    private String phone;

    private boolean registerAsHost;

    // --- Dang ky lam Cong tac vien (task 10) ---
    private boolean registerAsCollaborator;
    private String bankAccountNumber; // so TK nhan hoa hong, chi bat buoc neu registerAsCollaborator=true
    private String bankName;

    // --- Xac thuc OTP khi dang ky (task 9) ---
    // Nguoi dung chon 1 kenh nhan OTP (SMS / ZALO / EMAIL), bam nut "Gui ma" (goi AJAX
    // toi AuthController#guiOtp), sau do nhap ma nhan duoc vao truong otpCode roi moi submit form.
    private OtpChannel otpChannel = OtpChannel.EMAIL;
    private String otpCode;
}
