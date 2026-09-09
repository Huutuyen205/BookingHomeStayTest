package BookingHomeStay.BookingHomeStay.service.impl;

import BookingHomeStay.BookingHomeStay.entity.Booking;
import BookingHomeStay.BookingHomeStay.service.VietQrService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * QUAN TRONG - GIAI THICH CHO NGUOI PHAT TRIEN SAU:
 * Lop nay KHONG goi API co phi/can API-key cua ngan hang. No chi dung dich vu
 * anh QR mien phi cong khai cua VietQR.io (img.vietqr.io) de dung san 1 URL
 * <img>. Khi trinh duyet load URL nay, VietQR.io se ve ra ma QR chuan VietQR
 * (theo chuan EMVCo cua NAPAS) da nhung san: so tai khoan + ten chu TK + so
 * tien + noi dung. Nguoi dung CHI VIEC quet ma bang app ngan hang bat ky
 * (hoac MoMo) la moi thong tin duoc dien tu dong, khong can go tay.
 * Tai lieu: https://www.vietqr.io/danh-sach-api/
 *
 * Cau hinh 3 gia tri BANK_BIN / SO_TAI_KHOAN / TEN_CHU_TK trong application.properties
 * bang cac key: vietqr.bank-bin, vietqr.account-no, vietqr.account-name
 * (hien dang de gia tri demo, PHAI thay bang tai khoan ngan hang that truoc khi
 * dua len production).
 */
@Service
public class VietQrServiceImpl implements VietQrService {

    @Value("${vietqr.bank-bin:970422}") // 970436 = Vietcombank (demo). Doi theo ngan hang that.
    private String bankBin;

    @Value("${vietqr.account-no:058888192005}")
    private String accountNo;

    @Value("${vietqr.account-name:BOOKING HOMESTAY}")
    private String accountName;

    @Value("${vietqr.template:compact2}")
    private String template; // cac template co san cua VietQR: compact, compact2, qr_only, print

    @Override
    public String buildQrUrl(Booking booking) {
        BigDecimal amount = booking.getFinalAmount() != null ? booking.getFinalAmount() : BigDecimal.ZERO;
        String noiDung = "Thanh toan don " + booking.getBookingCode();

        String encodedAccountName = URLEncoder.encode(accountName, StandardCharsets.UTF_8);
        String encodedNoiDung = URLEncoder.encode(noiDung, StandardCharsets.UTF_8);

        // Vi du URL sinh ra:
        // https://img.vietqr.io/image/970436-0123456789-compact2.png?amount=1500000&addInfo=Thanh+toan+don+BK123&accountName=BOOKING+HOMESTAY
        return String.format(
                "https://img.vietqr.io/image/%s-%s-%s.png?amount=%s&addInfo=%s&accountName=%s",
                bankBin, accountNo, template,
                amount.toBigInteger().toString(),
                encodedNoiDung, encodedAccountName
        );
    }
}
