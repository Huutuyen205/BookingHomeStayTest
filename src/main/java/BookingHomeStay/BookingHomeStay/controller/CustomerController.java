package BookingHomeStay.BookingHomeStay.controller;

import BookingHomeStay.BookingHomeStay.dto.BookingRequest;
import BookingHomeStay.BookingHomeStay.entity.Booking;
import BookingHomeStay.BookingHomeStay.security.CustomUserDetails;
import BookingHomeStay.BookingHomeStay.service.BookingService;
import BookingHomeStay.BookingHomeStay.service.VietQrService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Chuc nang danh cho Khach hang. Duong dan goc: /khach-hang (truoc day la /customer).
 */
@Controller
@RequestMapping("/khach-hang")
@RequiredArgsConstructor
public class CustomerController {

    private final BookingService bookingService;
    private final VietQrService vietQrService;

    @GetMapping("/dat-phong/moi")
    public String newBookingForm(@RequestParam Long roomId, Model model) {
        BookingRequest form = new BookingRequest();
        form.setRoomId(roomId);
        model.addAttribute("bookingRequest", form);
        return "khach-hang/dat-phong-form";
    }

    @PostMapping("/dat-phong/xac-nhan")
    public String confirm(@Valid @ModelAttribute BookingRequest bookingRequest, BindingResult result,
                           @AuthenticationPrincipal CustomUserDetails currentUser, Model model) {
        if (result.hasErrors()) return "khach-hang/dat-phong-form";
        try {
            Booking booking = bookingService.createBooking(bookingRequest, currentUser.getId());
            model.addAttribute("booking", booking);
            // Sinh san URL anh QR VietQR de khach quet bang app ngan hang, tu dong dien
            // so tai khoan + so tien + noi dung chuyen khoan (khong can nhap tay). Xem VietQrService.
            model.addAttribute("qrCodeUrl", vietQrService.buildQrUrl(booking));
            return "khach-hang/dat-phong-thanh-cong";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("bookingRequest", bookingRequest);
            return "khach-hang/dat-phong-form";
        }
    }

    @GetMapping("/don-dat-phong")
    public String myBookings(@AuthenticationPrincipal CustomUserDetails currentUser, Model model) {
        model.addAttribute("bookings", bookingService.getMyBookings(currentUser.getId()));
        return "khach-hang/don-dat-phong";
    }

    @PostMapping("/don-dat-phong/{id}/huy")
    public String cancel(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails currentUser) {
        bookingService.cancelBooking(id, currentUser.getId());
        return "redirect:/khach-hang/don-dat-phong";
    }
}
