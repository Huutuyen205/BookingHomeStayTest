package BookingHomeStay.BookingHomeStay.service;

/**
 * Gui tin nhan qua Zalo Notification Service (ZNS) - kenh gui THEO SO DIEN
 * THOAI (khac voi kenh gui theo UID). Luu y: dau nam 2026 Zalo ra mat "ZBS
 * Template Message" thay the cho ca UID va ZNS cu, NHUNG luong gui theo SO
 * DIEN THOAI (ZNS) duoc Zalo xac nhan KHONG CAN doi API - van dung dung API
 * nay (xem https://business.openapi.zalo.me/message/template). Chi luong gui
 * theo UID moi phai chuyen sang API ZBS moi.
 *
 * De dung THAT can 1 Zalo Official Account (OA) da duoc duyet + 1 template
 * OTP da duoc Zalo duyet noi dung, va dien du 4 gia tri zalo.oa.* trong
 * application.properties. Neu chua dien du, sendOtp() se tra ve false va
 * OtpServiceImpl se tu dong ghi ma OTP ra console de van demo/test duoc.
 */
public interface ZaloZnsService {

    /**
     * Gui 1 tin nhan OTP toi so dien thoai qua ZNS.
     *
     * @param phoneNumber so dien thoai dang Viet Nam (vd "0987654321" hoac "84987654321")
     * @param otpCode     ma OTP 6 so can gui
     * @return true neu Zalo xac nhan da nhan tin gui thanh cong (error = 0),
     *         false neu chua cau hinh du OA/template hoac Zalo tra ve loi.
     */
    boolean sendOtp(String phoneNumber, String otpCode);
}
