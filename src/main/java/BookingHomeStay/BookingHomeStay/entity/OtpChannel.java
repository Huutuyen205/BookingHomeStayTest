package BookingHomeStay.BookingHomeStay.entity;

/**
 * Kenh nhan ma OTP khi dang ky tai khoan (task 9).
 * Nguoi dung chon 1 trong 3 luc dang ky; xem OtpService de biet cach mo rong
 * tich hop that voi tung nha cung cap (SMS Brandname, Zalo ZNS, SMTP email...).
 */
public enum OtpChannel {
    SMS, ZALO, EMAIL
}
