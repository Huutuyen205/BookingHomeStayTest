package BookingHomeStay.BookingHomeStay.service;

import BookingHomeStay.BookingHomeStay.entity.OtpChannel;

public interface OtpService {

    /**
     * Sinh ma OTP 6 so va gui toi dia chi (email/so dien thoai) qua kenh tuong ung.
     *
     * LUU Y (da sua loi thang 09/2026): truoc day ham nay tra ve "void", khien
     * Controller LUON bao "gui thanh cong" cho nguoi dung du kenh SMS/Zalo chua
     * cau hinh that hoac gui that bai - nguoi dung se cho mai ma khong bao gio
     * nhan duoc OTP that. Nay ham tra ve true/false PHAN ANH DUNG viec co that
     * su gui duoc qua kenh that hay khong, de giao dien bao chinh xac.
     *
     * @return true neu da gui THAT qua kenh that (email SMTP that/Zalo ZNS that),
     *         false neu kenh chua duoc cau hinh (chi ghi ma ra console de demo).
     *         Ca 2 truong hop ma OTP sinh ra deu duoc luu lai va xac thuc binh
     *         thuong bang verifyOtp() - false chi co nghia "chua gui duoc toi
     *         nguoi dung that", khong co nghia "ma khong hop le".
     */
    boolean sendOtp(String destination, OtpChannel channel);

    /** Kiem tra ma OTP nguoi dung nhap co dung va con han khong. Dung 1 lan roi bi xoa. */
    boolean verifyOtp(String destination, String code);
}
