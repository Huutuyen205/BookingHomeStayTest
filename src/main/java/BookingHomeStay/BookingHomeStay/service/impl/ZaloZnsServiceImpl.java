package BookingHomeStay.BookingHomeStay.service.impl;

import BookingHomeStay.BookingHomeStay.service.ZaloZnsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * ===================== TICH HOP THAT ZALO ZNS (gui OTP qua Zalo) =====================
 * Dung dung API chinh thuc cua Zalo Business (khong qua ben thu 3 trung gian):
 *   1) Lam moi access_token: POST https://oauth.zaloapp.com/v4/oa/access_token
 *   2) Gui tin nhan template (OTP): POST https://business.openapi.zalo.me/message/template
 *
 * DE DUNG THAT CAN 4 THU (dien trong application.properties, muc zalo.oa.*):
 *   - app-id, app-secret       : lay tu https://developers.zalo.me (tao App loai "Official Account")
 *   - refresh-token            : lay 1 lan dau tien qua buoc "Get Access Token" thu cong
 *                                 (Zalo yeu cau quet QR xac nhan chu OA 1 lan duy nhat)
 *   - otp-template-id          : ID cua 1 template OTP DA DUOC ZALO DUYET NOI DUNG
 *                                 (khong the tu bia noi dung, phai gui Zalo duyet truoc)
 *
 * LUU Y QUAN TRONG VE REFRESH TOKEN: moi lan lam moi access_token, Zalo tra ve
 * LUON 1 refresh_token MOI (refresh_token cu se het hieu luc). Code ben duoi tu
 * dong dung refresh_token moi cho lan sau TRONG THOI GIAN UNG DUNG DANG CHAY
 * (luu trong bien nho RAM), nhung neu RESTART ung dung ma khong cap nhat lai
 * gia tri moi nhat vao application.properties thi se dung lai refresh_token cu
 * da het han -> gui loi. Neu can chay on dinh lau dai qua nhieu lan restart,
 * nen luu refresh_token vao bang trong database thay vi file .properties (co
 * the lam them 1 bang "zalo_oa_token" rieng neu ban can - hien tai de RAM cho
 * don gian, phu hop demo/1 server chay lien tuc).
 *
 * VE DOT CHUYEN DOI ZBS 2026: dau nam 2026 Zalo hop nhat UID + ZNS thanh "ZBS
 * Template Message", NHUNG thong bao chinh thuc cua Zalo ghi ro "Luong gui tin
 * qua SDT (ZNS): Khong can thay doi" - nen API/endpoint duoi day VAN DUNG,
 * khong bi anh huong boi dot chuyen doi nay.
 * =======================================================================================
 */
@Slf4j
@Service
public class ZaloZnsServiceImpl implements ZaloZnsService {

    private static final String OAUTH_URL = "https://oauth.zaloapp.com/v4/oa/access_token";
    private static final String SEND_URL = "https://business.openapi.zalo.me/message/template";

    private final RestTemplate restTemplate;

    @Value("${zalo.oa.app-id:}")
    private String appId;

    @Value("${zalo.oa.app-secret:}")
    private String appSecret;

    @Value("${zalo.oa.refresh-token:}")
    private String configuredRefreshToken;

    @Value("${zalo.oa.otp-template-id:}")
    private String otpTemplateId;

    // Cache trong RAM: access_token hien tai + thoi diem het han + refresh_token
    // moi nhat (Zalo xoay vong refresh_token moi lan lam moi).
    private volatile String cachedAccessToken;
    private volatile Instant accessTokenExpireAt = Instant.EPOCH;
    private volatile String currentRefreshToken;

    public ZaloZnsServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private boolean isConfigured() {
        return !appId.isBlank() && !appSecret.isBlank()
                && !configuredRefreshToken.isBlank() && !otpTemplateId.isBlank();
    }

    @Override
    public synchronized boolean sendOtp(String phoneNumber, String otpCode) {
        if (!isConfigured()) {
            log.warn("[ZALO-ZNS] CHUA CAU HINH DU zalo.oa.app-id / app-secret / refresh-token / "
                    + "otp-template-id trong application.properties - khong the gui that. Ma OTP demo: {} -> {}",
                    otpCode, phoneNumber);
            return false;
        }

        String accessToken = getValidAccessToken();
        if (accessToken == null) {
            log.error("[ZALO-ZNS] Khong lay duoc access_token (kiem tra lai app-id/app-secret/refresh-token) - "
                    + "khong gui duoc ma {} toi {}", otpCode, phoneNumber);
            return false;
        }

        String phoneChuanHoa = chuanHoaSoDienThoai(phoneNumber);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("access_token", accessToken);

            Map<String, Object> body = Map.of(
                    "phone", phoneChuanHoa,
                    "template_id", otpTemplateId,
                    "template_data", Map.of("otp", otpCode),
                    "tracking_id", UUID.randomUUID().toString()
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            Map<?, ?> response = restTemplate.postForObject(SEND_URL, request, Map.class);

            if (response == null) {
                log.error("[ZALO-ZNS] Khong nhan duoc phan hoi tu Zalo khi gui toi {}", phoneChuanHoa);
                return false;
            }
            Object errorCode = response.get("error");
            boolean thanhCong = errorCode != null && (errorCode.equals(0) || "0".equals(String.valueOf(errorCode)));
            if (thanhCong) {
                log.info("[ZALO-ZNS] Da gui THAT toi {}", phoneChuanHoa);
            } else {
                // Cac ma loi thuong gap: -124 (template chua duyet), -32 (het quota),
                // -216 (so dien thoai khong hop le / chua co Zalo) - xem chi tiet
                // trong response.message tra ve tu Zalo.
                log.error("[ZALO-ZNS] Zalo tu choi gui - error={} message={}", errorCode, response.get("message"));
            }
            return thanhCong;
        } catch (RestClientException e) {
            log.error("[ZALO-ZNS] Loi ket noi toi Zalo khi gui ma toi {}: {}", phoneChuanHoa, e.getMessage());
            return false;
        }
    }

    /** Tra ve access_token con hieu luc, tu dong lam moi truoc khi het han 60 giay. */
    private String getValidAccessToken() {
        if (cachedAccessToken != null && Instant.now().isBefore(accessTokenExpireAt.minusSeconds(60))) {
            return cachedAccessToken;
        }
        return refreshAccessToken();
    }

    private String refreshAccessToken() {
        String refreshTokenDeDung = (currentRefreshToken != null) ? currentRefreshToken : configuredRefreshToken;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("secret_key", appSecret);

            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("app_id", appId);
            form.add("refresh_token", refreshTokenDeDung);
            form.add("grant_type", "refresh_token");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);
            Map<?, ?> response = restTemplate.postForObject(OAUTH_URL, request, Map.class);

            if (response == null || response.get("access_token") == null) {
                log.error("[ZALO-ZNS] Lam moi access_token THAT BAI - phan hoi: {}", response);
                return null;
            }

            cachedAccessToken = String.valueOf(response.get("access_token"));
            // Zalo cap refresh_token MOI moi lan lam moi - luu lai de dung cho lan sau.
            if (response.get("refresh_token") != null) {
                currentRefreshToken = String.valueOf(response.get("refresh_token"));
                log.warn("[ZALO-ZNS] Zalo da cap refresh_token MOI. Neu ung dung restart, hay cap nhat "
                        + "gia tri zalo.oa.refresh-token trong application.properties thanh: {}", currentRefreshToken);
            }
            long expiresInSeconds = 3600; // Zalo mac dinh access_token song 1 gio
            try {
                expiresInSeconds = Long.parseLong(String.valueOf(response.get("expires_in")));
            } catch (NumberFormatException ignored) {
                // giu gia tri mac dinh 3600 neu Zalo khong tra ve so hop le
            }
            accessTokenExpireAt = Instant.now().plusSeconds(expiresInSeconds);
            log.info("[ZALO-ZNS] Da lam moi access_token, het han sau {} giay", expiresInSeconds);
            return cachedAccessToken;
        } catch (RestClientException e) {
            log.error("[ZALO-ZNS] Loi ket noi toi Zalo khi lam moi access_token: {}", e.getMessage());
            return null;
        }
    }

    /** Doi so dien thoai Viet Nam ve dang quoc te Zalo yeu cau (bo "0" dau, them "84"). */
    private String chuanHoaSoDienThoai(String soDienThoai) {
        String so = soDienThoai.replaceAll("[^0-9]", "");
        if (so.startsWith("0")) {
            return "84" + so.substring(1);
        }
        if (so.startsWith("84")) {
            return so;
        }
        return "84" + so;
    }
}
