package BookingHomeStay.BookingHomeStay.service;

import BookingHomeStay.BookingHomeStay.entity.Booking;

public interface VietQrService {
    /**
     * Tra ve URL anh QR (chuan VietQR) de nhung vao the <img>.
     * Khach chi can mo app Ngan hang / MoMo... quet ma la tu dong dien san:
     * so tai khoan, ten chu tai khoan, so tien va noi dung chuyen khoan (ma don hang).
     */
    String buildQrUrl(Booking booking);
}
