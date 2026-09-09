package BookingHomeStay.BookingHomeStay.service.impl;

import BookingHomeStay.BookingHomeStay.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service email dung chung cho ca he thong (khac voi OtpServiceImpl chi chuyen
 * gui ma OTP). Dung cho: email "dang ky thanh cong" (task 8), email "xin danh
 * gia sau khi tra phong" (task 9), va co the mo rong them sau nay (email xac
 * nhan don, email nhac lich...).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String mailFrom;

    @Override
    public void send(String to, String subject, String body) {
        if (mailFrom == null || mailFrom.isBlank() || to == null || to.isBlank()) {
            log.warn("[EMAIL] Chua cau hinh SMTP that hoac thieu dia chi nhan - chi log: to={}, subject={}", to, subject);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailFrom);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("[EMAIL] Da gui toi {} - {}", to, subject);
        } catch (MailException e) {
            log.error("[EMAIL] Gui that bai toi {}: {}", to, e.getMessage());
        }
    }
}
