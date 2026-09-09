package BookingHomeStay.BookingHomeStay.service.impl;

import BookingHomeStay.BookingHomeStay.entity.OtpChannel;
import BookingHomeStay.BookingHomeStay.service.OtpService;
import BookingHomeStay.BookingHomeStay.service.ZaloZnsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * ===================== CAP NHAT 09/2026: PHAT TRIEN LAI KENH EMAIL + ZALO =====================
 * Kenh EMAIL: da hoat dong that tu dot truoc (can dien spring.mail.* that).
 *
 * Kenh ZALO: TRUOC DAY chi log ra console, gio da noi that voi ZaloZnsService
 * (goi API chinh thuc cua Zalo Business qua OAuth token + endpoint ZNS). Chi
 * can dien du 4 gia tri zalo.oa.* trong application.properties la gui THAT.
 *
 * Kenh SMS: VAN CHUA gui that duoc - day la dich vu CO PHI rieng (eSMS,
 * Speedsms, Twilio...) ma ban phai tu dang ky hop dong + mua API key rieng,
 * khong co API mien phi nao de tich hop thay the. Da danh san vi tri
 * "TODO: TICH HOP THAT" trong sendViaSms() de dien khi ban co tai khoan that.
 *
 * LOI DA SUA (quan trong): truoc day sendOtp() la "void" nen dung frontend
 * (dang-ky.html) LUON hien "Da gui ma OTP thanh cong" mau xanh CHO DU that ra
 * chua gui duoc gi ca (vi kenh SMS/Zalo/Email chua cau hinh, hoac gui that
 * bai). Nguoi dung se cho OTP mai khong bao gio den. Nay tra ve true/false
 * THAT SU phan anh ket qua gui, AuthController va giao dien da cap nhat theo
 * de bao dung mau xanh/do tuong ung.
 * ================================================================================================
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final JavaMailSender mailSender;
    private final ZaloZnsService zaloZnsService;

    @Value("${spring.mail.username:}")
    private String mailFrom;

    private record OtpEntry(String code, Instant expireAt) {}

    private final Map<String, OtpEntry> otpStore = new ConcurrentHashMap<>();
    private static final Duration TTL = Duration.ofMinutes(5);

    @Override
    public boolean sendOtp(String destination, OtpChannel channel) {
        String code = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 999999));
        otpStore.put(destination, new OtpEntry(code, Instant.now().plus(TTL)));

        return switch (channel) {
            case EMAIL -> sendViaEmail(destination, code);
            case SMS -> sendViaSms(destination, code);
            case ZALO -> sendViaZalo(destination, code);
        };
    }

    @Override
    public boolean verifyOtp(String destination, String code) {
        OtpEntry entry = otpStore.get(destination);
        if (entry == null) return false;
        boolean hopLe = entry.code().equals(code) && Instant.now().isBefore(entry.expireAt());
        if (hopLe) otpStore.remove(destination);
        return hopLe;
    }

    private boolean sendViaEmail(String email, String code) {
        if (mailFrom == null || mailFrom.isBlank()) {
            // Chua dien spring.mail.username trong application.properties -> khong
            // the gui that, fallback ve log de khong lam sap luong dang ky khi demo.
            log.warn("[OTP-EMAIL] CHUA CAU HINH spring.mail.* - chi log ra console. Ma: {} -> {}", code, email);
            return false;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailFrom);
            message.setTo(email);
            message.setSubject("[BookingHomeStay] Mã xác thực OTP của bạn");
            message.setText("Mã xác thực (OTP) của bạn là: " + code
                    + "\nMã có hiệu lực trong 5 phút. Vui lòng không chia sẻ mã này cho bất kỳ ai.");
            mailSender.send(message);
            log.info("[OTP-EMAIL] Da gui THAT toi {}", email);
            return true;
        } catch (MailException e) {
            // Sai username/password SMTP, hoac chua bat "App Password" cho Gmail...
            log.error("[OTP-EMAIL] Gui that BAO LOI: {} - kiem tra lai cau hinh spring.mail.* trong application.properties", e.getMessage());
            return false;
        }
    }

    private boolean sendViaSms(String phone, String code) {
        // TODO: TICH HOP THAT - can dang ky dich vu SMS Brandname co phi (eSMS/Speedsms/Twilio).
        // Khong co API mien phi de thay the - day la gioi han cua nha cung cap dich vu.
        log.info("[OTP-SMS] (DEMO - chua co dich vu SMS that) Gui ma {} den so dien thoai {}", code, phone);
        return false;
    }

    private boolean sendViaZalo(String phone, String code) {
        return zaloZnsService.sendOtp(phone, code);
    }
}
