package BookingHomeStay.BookingHomeStay.service;

public interface EmailService {
    /** Gui 1 email don gian (text thuan). Neu chua cau hinh SMTP that thi chi log ra console. */
    void send(String to, String subject, String body);
}
