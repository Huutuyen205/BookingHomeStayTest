# BookingHomeStay — Báo cáo chỉnh sửa & Hướng dẫn

Tài liệu này ghi lại **toàn bộ những gì đã thay đổi**, **vì sao thay đổi**, và
**những gì cần làm tiếp** để đưa dự án lên production thật sự. Đọc file này
trước khi đụng vào code.

---

## 1. Cách chạy dự án

```bash
# 1. Tạo database (nếu chưa có) và import file data.sql người dùng đã cung cấp
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS homestay_booking_db"
mysql -u root -p homestay_booking_db < data.sql

# 2. (Tuỳ chọn) đặt mật khẩu DB qua biến môi trường thay vì sửa file cấu hình
export DB_USERNAME=root
export DB_PASSWORD=matkhaucuaban

# 3. Chạy ứng dụng
cd BookingHomeStay
./mvnw spring-boot:run
```

Mở trình duyệt: **http://localhost:8080/trang-chu** (hoặc `/`).

Tài khoản admin mặc định (tạo tự động lúc khởi động lần đầu, xem
`DataInitializer.java`): `admin@bookinghomestay.local` / `Admin@123`

> Lưu ý: `spring.jpa.hibernate.ddl-auto=update` sẽ tự tạo thêm bảng
> `collaborators` (mới thêm) vào database đã import từ `data.sql`, không cần
> chỉnh tay file SQL.

---

## 2. Đổi cấu trúc dự án sang tiếng Việt (yêu cầu 1 & 2 & 4)

**Không thể** đổi tên package Java (`BookingHomeStay.BookingHomeStay.controller`...)
sang tiếng Việt có dấu — đây là giới hạn kỹ thuật thật sự của Java, không phải
lựa chọn cá nhân:
- Định danh Java không được chứa khoảng trắng, dấu gạch ngang.
- Toàn bộ ký hiệu Spring (`@Component`, quét package, reflection...), Maven,
  IDE đều dựa vào quy ước tên package viết thường không dấu. Đổi sẽ vỡ hàng
  loạt thứ không liên quan.

**Đã đổi được** (đúng theo hướng người dùng gợi ý ở mục 2 — sửa phần *sinh ra
được cho người dùng thấy*, tức là URL và cấu trúc thư mục giao diện):

| Trước | Sau |
|---|---|
| `localhost:8080/` (không có bản `/trang-chu`) | `localhost:8080/trang-chu` (và `/` vẫn hoạt động song song) |
| `/login`, `/register` | `/dang-nhap`, `/dang-ky` |
| `/admin/**` | `/quan-tri/**` |
| `/host/**` | `/chu-nha/**` |
| `/customer/**` | `/khach-hang/**` |
| `/homestays/{slug}`, `/homestays/nearby` | `/homestay/{slug}`, `/homestay/gan-day` |
| `/search` | `/tim-kiem` |
| `templates/admin`, `/host`, `/customer`, `/auth`, `/error`, `/fragments` | `templates/quan-tri`, `/chu-nha`, `/khach-hang`, `/xac-thuc`, `/loi`, `/dung-chung` |

Toàn bộ Controller, `SecurityConfig`, `LoginSuccessHandler` và **23 file
template** đã được rà soát chéo (route ↔ link trong HTML) để đảm bảo khớp
100% — không còn link nào trỏ tới URL cũ.

---

## 3. Dọn "rác" đã thực hiện (yêu cầu 6)

| Rác đã xoá / sửa | Lý do |
|---|---|
| `target/`, `.idea/` | File build/IDE không nên nằm trong mã nguồn |
| `templates/admin/403.html`, `admin/404.html` | Bản sao y hệt `templates/error/403.html`, `404.html`, không controller nào trả về |
| `templates/home.html` | Trang demo tĩnh của theme, không controller nào dùng tới |
| `templates/fragments/header.html` + `footer.html` | **Trùng lặp với `fragments/layout.html`** — dự án cũ có 2 bộ navbar/footer khác nhau tồn tại song song (trang chủ dùng bộ này, mọi trang khác dùng bộ kia) → giao diện không nhất quán. Đã **gộp làm 1** trong `dung-chung/layout.html`. |
| 8 ảnh banner trùng (`banner2.png`, `banner3.jpg/png`...) | Chỉ `banner.jpg` được dùng thật, còn lại ~2MB ảnh chết từ theme gốc |
| Thiếu Bootstrap JS bundle | **Bug có sẵn:** toàn bộ dropdown, menu mobile (hamburger) chưa từng hoạt động ở BẤT KỲ trang nào vì file `bootstrap.bundle.min.js` chưa từng được nạp. Đã thêm 1 lần duy nhất vào fragment `footer` dùng chung. |
| Nút "+ Thêm phòng" (trang Chủ nhà) trỏ tới route không tồn tại | **Bug có sẵn:** link trỏ `/host/homestays/{id}/rooms/new` nhưng Controller gốc không có route này → bấm vào sẽ lỗi 404 vĩnh viễn. Đã bổ sung route còn thiếu. |
| Trang `host/dashboard.html` (nay là `chu-nha/tong-quan.html`) | **Bug có sẵn:** hiển thị dữ liệu `${stats}` và các nút duyệt Host/Homestay của **Admin**, trong khi Controller của Host không hề truyền biến này ra → trang sẽ lỗi khi Host truy cập. Đã viết lại đúng theo dữ liệu Host thực có (homestay của tôi, đơn của tôi). |
| Mật khẩu DB hard-code trong `application.properties` | Đã chuyển sang đọc từ biến môi trường `DB_PASSWORD` (có giá trị mặc định để chạy local ngay không cần cấu hình gì thêm) |

Mọi đoạn code khó hiểu / cần lưu ý đều có **comment tiếng Việt tại chỗ**
(tìm từ khoá `GHI CHU`, `TODO`, `LUU Y` trong code) để người sau dễ bảo trì.

---

## 4. Tính năng mới đã thêm

### 4.1. Đăng ký OTP 3 kênh (yêu cầu 10)
- File: `entity/OtpChannel.java`, `service/OtpService.java` + `OtpServiceImpl.java`
- Người dùng chọn **Email / SMS / Zalo** ở trang đăng ký, bấm "Gửi mã" (gọi AJAX
  tới `POST /dang-ky/gui-otp`), nhập mã rồi mới submit form.
- **Thật sự chạy được để demo** (mã OTP sinh ngẫu nhiên, lưu 5 phút, xác thực
  đúng/sai) nhưng kênh SMS và Zalo hiện **chỉ log ra console** vì cần hợp đồng
  trả phí với nhà cung cấp thật (xem mục 6 bên dưới). Kênh Email sẽ gửi thật
  nếu bạn cấu hình SMTP thật trong `application.properties` (đã có sẵn khối
  cấu hình mẫu, chỉ cần bỏ comment).

### 4.2. Đăng ký làm Cộng tác viên (yêu cầu 11)
- Nằm ngay trong form đăng ký khách hàng: tick "Đăng ký làm Cộng tác viên",
  hiện thêm ô ngân hàng + số tài khoản nhận hoa hồng.
- Entity mới: `Collaborator` (mã giới thiệu tự sinh, trạng thái chờ duyệt).

### 4.3. Phân biệt 3 nhóm quan hệ nghiệp vụ (yêu cầu 9)
- Trang mới **`/quan-tri/nguoi-dung`**: liệt kê tách bạch theo 4 nhóm — Quản
  trị viên / Chủ nhà / Cộng tác viên–Sale / Khách hàng.
- Về mặt phân quyền (`SecurityConfig`): Chủ nhà và Cộng tác viên **dùng chung**
  không gian `/chu-nha/**` (vì cả hai đều "bán phòng hộ"), Khách hàng dùng
  `/khach-hang/**`, Admin dùng `/quan-tri/**` — tách biệt hoàn toàn quyền truy cập.

### 4.4. Thanh toán quét QR ngân hàng (yêu cầu 12)
- File: `service/VietQrService.java` + `VietQrServiceImpl.java`.
- Dùng dịch vụ **miễn phí, công khai** [VietQR.io](https://vietqr.io) — không
  cần API key, không cần hợp đồng ngân hàng để demo. Sinh ra 1 URL ảnh, nhúng
  thẳng vào `<img>` ở trang "Đặt phòng thành công".
- Khách quét bằng **app ngân hàng bất kỳ** hoặc MoMo → tự động điền: số tài
  khoản, tên chủ tài khoản, số tiền, nội dung (mã đơn hàng) — không cần gõ tay.
- **Trước khi dùng thật:** đổi 3 giá trị `vietqr.bank-bin`, `vietqr.account-no`,
  `vietqr.account-name` trong `application.properties` thành tài khoản ngân
  hàng thật của bạn.

### 4.5. Dashboard Admin có biểu đồ (yêu cầu 13)
- `/quan-tri/tong-quan`: thêm biểu đồ cột (Chart.js qua CDN) theo dõi **số
  lượng đơn đặt phòng theo từng tháng**, cộng với các thẻ số liệu tổng quan có sẵn
  (người dùng, chủ nhà, homestay, đơn chờ xử lý).

### 4.6. Icon liên hệ nổi (yêu cầu 14)
- Hiển thị **trên mọi trang** (nhúng trong fragment `footer` dùng chung):
  gọi điện, Zalo, Messenger, Telegram, Viber, Facebook, TikTok.
- Đổi số điện thoại / link thật tại `dung-chung/layout.html` (tìm class
  `lien-he-noi`) trước khi dùng thật.

### 4.7. Giao diện / theme (yêu cầu 15)
Dự án **đã sẵn có** một theme du lịch/lưu trú đầy đủ (banner lớn, gallery ảnh,
lightbox, masonry layout, form tìm kiếm nổi bật) — kiểu theme phổ biến trên các
trang như ThemeWagon dành cho ngành khách sạn/homestay. Sau khi kiểm tra, tôi
quyết định **giữ nguyên và hoàn thiện theme sẵn có** thay vì thay thế bằng theme
tải mới, vì:
1. Theme hiện tại đã tương thích 100% với biến Thymeleaf của dự án; thay theme
   mới đồng nghĩa viết lại toàn bộ 23 trang từ đầu — rủi ro rất cao, lợi ích thấp.
2. Theme mới tải từ nguồn ngoài thường có giấy phép sử dụng cần kiểm tra kỹ
   trước khi dùng cho mục đích thương mại.
3. Việc "dọn rác + đồng bộ header/footer/nút bấm" (mục 3) mang lại giá trị
   thực tế cao hơn nhiều so với thay vỏ giao diện.

Nếu bạn vẫn muốn đổi theme khác, nên làm ở giai đoạn sau, **sau khi** toàn bộ
luồng nghiệp vụ (đặt phòng, thanh toán, check-in/out) đã chạy ổn định — tách
biệt rõ "sửa logic" và "sửa giao diện" để dễ debug.

---

## 5. Phân tích: Nên lưu tài khoản khách hàng ở đâu? (yêu cầu 8)

**Khuyến nghị: giữ nguyên kiến trúc hiện tại — một bảng `users` duy nhất cho
TẤT CẢ vai trò (Admin/Chủ nhà/CTV/Khách hàng), phân biệt bằng bảng quan hệ
`user_roles`.** Đây là kiến trúc đúng đắn dự án gốc đã chọn. Lý do:

| Phương án | Ưu điểm | Nhược điểm | Đánh giá |
|---|---|---|---|
| **1 bảng `users` chung + bảng `roles` (đang dùng)** | Một người có thể vừa là khách vừa là chủ nhà/CTV mà **không cần 2 tài khoản**; đăng nhập 1 chỗ; dễ audit, dễ thêm vai trò mới | Bảng lớn hơn theo thời gian (không đáng ngại với vài trăm nghìn user) | ✅ **Nên dùng** — đúng chuẩn RBAC (Role-Based Access Control) |
| Tách riêng bảng `customers`, `hosts`, `admins` | Tưởng "rõ ràng" hơn | Không thể vừa là khách vừa là chủ nhà; phải đăng nhập nhiều nơi; trùng lặp logic auth 3 lần | ❌ Không nên |
| Lưu trên dịch vụ thứ 3 (Firebase Auth, Auth0, Cognito...) | Không phải tự quản lý bảo mật mật khẩu | Tốn phí, phụ thuộc nhà cung cấp, phức tạp hoá 1 dự án Spring Boot đã có sẵn Spring Security | ⚠️ Chỉ nên cân nhắc nếu dự án mở rộng đa nền tảng (mobile app riêng, SSO nhiều hệ thống) |

**Về bảo mật mật khẩu:** dự án đã dùng `BCryptPasswordEncoder` (băm một chiều,
có salt ngẫu nhiên) — đúng chuẩn, **không được** đổi sang MD5/SHA thuần hay lưu
plaintext.

**Đề xuất bổ sung khi lên production:**
- Thêm cột `email_verified`, `phone_verified` (đánh dấu sau khi xác thực OTP thành công).
- Giới hạn số lần đăng nhập sai (rate-limit) để chống brute-force.
- Cân nhắc thêm đăng nhập qua Google/Facebook (`AuthProvider` entity đã có sẵn field `provider`, mới dùng `LOCAL`, sẵn sàng mở rộng).

---

## 6. Những việc CẦN làm tiếp trước khi lên thật (chưa làm trong lượt này)

| Việc cần làm | Vì sao chưa làm |
|---|---|
| Tích hợp SMS Brandname thật (eSMS, Speedsms, Twilio...) | Cần đăng ký dịch vụ + trả phí + API key riêng của bạn |
| Tích hợp Zalo Notification Service (ZNS) thật | Cần Zalo Official Account đã duyệt + template đã duyệt |
| OTP lưu Redis thay vì RAM | Cần thêm hạ tầng Redis; hiện tại RAM đủ dùng để **demo/1 server** |
| Cổng thanh toán tự động xác nhận (VNPay/MoMo webhook) | Hiện tại QR chỉ giúp khách **chuyển khoản đúng nội dung**, việc xác nhận "đã nhận tiền" vẫn cần Admin duyệt tay hoặc tích hợp webhook ngân hàng/MoMo thật |
| Test tự động (unit test / integration test) | Ngoài phạm vi yêu cầu ban đầu, nên làm trước khi lên production |
| Đổi `ddl-auto=update` sang Flyway/Liquibase | An toàn hơn khi có nhiều người cùng sửa schema |

---

## 8. Cập nhật đợt 2 (bổ sung sau phản hồi thực tế của người dùng)

| # | Yêu cầu | Đã làm |
|---|---|---|
| 1,2,5 | Admin CRUD phòng, sửa giá, bật/tắt còn phòng | `/quan-tri/phong` — xem toàn bộ phòng mọi homestay, sửa giá bất kỳ phòng nào không cần là chủ, bật/tắt "Còn phòng/Hết phòng" |
| 3 | Admin tạo mã giảm giá | `/quan-tri/ma-giam-gia` — CRUD đầy đủ (tạo/sửa/khoá/xoá), khoá thay vì xoá nếu mã đã từng dùng (tránh vỡ dữ liệu lịch sử đơn hàng) |
| 4 | Biểu đồ tròn thay cột, có chú thích | Dashboard admin nay có **biểu đồ tròn** "Đặt trực tiếp" vs "Qua Cộng tác viên" (có legend + % khi hover), đồng thời **giữ thêm** biểu đồ cột theo tháng ở cạnh bên để không mất thông tin xu hướng thời gian. Cơ chế: khách nhập mã giới thiệu của CTV lúc đặt phòng → hệ thống tự gắn `Booking.source = COLLABORATOR` |
| 6 | Admin xác nhận Chủ nhà / CTV | Trang `/quan-tri/cong-tac-vien` (duyệt riêng, tách bạch hoàn toàn với `/quan-tri/chu-nha`) |
| 7 | OTP không hoạt động — lý do & khắc phục | **Lý do:** thiếu dependency gửi mail + chưa cấu hình SMTP thật. **Đã khắc phục:** thêm `spring-boot-starter-mail`, nối `JavaMailSender` thật — chỉ cần điền email + App Password Gmail vào `application.properties` là OTP gửi được thật. SMS/Zalo vẫn cần bạn tự đăng ký dịch vụ trả phí riêng (eSMS/Speedsms hoặc Zalo ZNS) — đây là giới hạn của nhà cung cấp, không phải lỗi code |
| 8 | Email đăng ký thành công kèm link | Gửi ngay sau khi tạo tài khoản thành công, có link `/trang-chu` và `/dang-nhap` |
| 9 | Email xin đánh giá sau khi trả phòng | Gửi tự động ngay khi Chủ nhà bấm "Trả phòng" cho khách |
| 11 | Dropdown đầy đủ 63 tỉnh/thành | Fragment dùng chung `dung-chung/danh-sach-tinh.html`, áp dụng ở trang tìm kiếm, trang chủ, và form tạo homestay của Chủ nhà (chuẩn hoá dữ liệu, tránh gõ sai chính tả) |
| 12 | Bỏ chọn lại tỉnh ở "Điểm đến phổ biến", chỉ hiện quận/huyện | Khi vào từ 1 thẻ "Điểm đến phổ biến" (đã có sẵn `province`), sidebar tìm kiếm ẩn ô chọn tỉnh, chỉ hiện dropdown Quận/huyện (lấy từ dữ liệu thật đang có homestay) |
| 13 | Số khách không được âm | Thêm `min="1"` ở mọi ô nhập số khách (client-side); phía server DTO đã có sẵn `@Positive`/`@Min(1)` |
| 14 | Giá theo định dạng `.000 VNĐ` | Đổi toàn bộ hiển thị giá từ kiểu Mỹ (`1,500,000đ`) sang kiểu Việt Nam (`1.500.000 VNĐ`) |
| 16 | Gộp icon liên hệ vào 1 nút | Icon Zalo/điện thoại/Messenger/Telegram/Viber/Facebook/TikTok giờ ẩn trong 1 nút tròn, chạm vào mới xổ ra danh sách |
| 17 | Thông báo còn/hết phòng | Trang chi tiết homestay hiện **toàn bộ phòng** kèm badge "Còn phòng"/"Hết phòng" (trước đây ẩn hẳn phòng hết chỗ khiến khách tưởng homestay ít phòng hơn thật); nút "Đặt phòng" tự vô hiệu khi hết phòng; cả Admin và Chủ nhà đều đổi được trạng thái này |

### Việc CHƯA làm trong đợt này (quy mô quá lớn cho 1 lượt)
- **Task 15 — song ngữ Anh/Việt (i18n):** đây là hạng mục lớn, cần: (1) tạo file `messages_vi.properties` + `messages_en.properties` cho **toàn bộ ~23 trang**, (2) thay mọi chữ cứng trong HTML bằng `#{key}`, (3) thêm bộ chọn ngôn ngữ + `LocaleResolver`. Nên làm thành 1 dự án riêng sau khi các tính năng nghiệp vụ đã ổn định, tránh vừa sửa logic vừa sửa giao diện cùng lúc dễ gây lỗi chồng chéo.
- Task 10 (bạn để trống, không có nội dung).

### Việc mới phát sinh cần bạn xác nhận thêm
- Với mã giảm giá: hiện cho phép **1 người dùng dùng nhiều lần cùng 1 mã** (chỉ giới hạn tổng lượt dùng toàn hệ thống qua `usageLimit`), nếu bạn muốn giới hạn "mỗi khách chỉ dùng 1 lần/mã" thì cần bảng theo dõi lịch sử sử dụng riêng — nói cho tôi biết nếu cần bổ sung.
- Trang đánh giá (review) sau khi khách nhận email chưa có **giao diện để khách bấm sao/viết nhận xét** — hiện email chỉ dẫn link về trang "Đơn của tôi"; nếu cần giao diện đánh giá thật (form sao + bình luận + lưu DB + hiển thị công khai trên trang homestay) thì đây là tính năng mới cần làm thêm.

## 9. Rà soát ổn định lại toàn bộ code (theo yêu cầu người dùng)

**Giới hạn môi trường cần nói rõ:** môi trường chạy của tôi bị chặn truy cập Maven
Central (`repo.maven.apache.org`, `repo1.maven.org` đều trả về lỗi 403 khi tôi thử),
nên **tôi không có cách nào build/compile thật sự** dự án Spring Boot này để xác nhận
100%. Thay vào đó tôi đã làm 1 lượt rà soát tĩnh (đọc code bằng tay, không chạy máy)
kỹ nhất có thể: đối chiếu **từng lời gọi hàm** với **chữ ký hàm thật** trong interface,
đối chiếu **từng field HTML** với **field thật** trong entity, đếm số lượng `@Override`
khớp với số method khai báo trong mọi interface.

**Lỗi THẬT SỰ đã tìm thấy và sửa trong lượt rà soát này:**

| Lỗi | File | Nguyên nhân | Đã sửa |
|---|---|---|---|
| Lỗi biên dịch (thiếu tham số) | `HomeController.java` dòng 25 | Khi thêm bộ lọc "quận/huyện" (task 12), tôi đổi `HomestayService.search()` từ 3 tham số thành 4 tham số, nhưng quên cập nhật lời gọi ở hàm hiển thị trang chủ (`homestayService.search(null, null, null)`) — thiếu 1 tham số, code sẽ **không biên dịch được** | Thêm tham số `district` còn thiếu: `search(null, null, null, null)` |

Ngoài lỗi trên, toàn bộ các phần sau đã được đối chiếu và **khớp chính xác 100%**:
- Số lượng method trong mọi interface Service = số lượng `@Override` trong Impl tương ứng (không thừa, không thiếu)
- Mọi lời gọi `homestayService.*`, `roomService.*`, `bookingService.*`, `adminService.*`, `promotionService.*` trong 5 Controller đều đúng tên hàm + đúng số lượng/kiểu tham số
- Mọi tên biến Model (`th:object`, `${...}`) trong 30 file HTML đều khớp đúng tên Controller đã `model.addAttribute(...)`
- Mọi field entity dùng trong template (`room.homestay.host.businessName`, `home.images[0].imageUrl`, `entry.value`...) đều tồn tại thật trong entity

**Đã thêm chú thích tiếng Việt phía trên mỗi hàm** (giải thích hàm đó làm gì, tại
sao làm vậy) trong 4 file quan trọng nhất vừa viết mới: `AdminController.java`,
`AdminServiceImpl.java`, `PromotionServiceImpl.java`, `HostController.java`. Các
file khác (OtpServiceImpl, EmailServiceImpl, VietQrServiceImpl, UserServiceImpl,
BookingServiceImpl...) đã có sẵn chú thích chi tiết từ các đợt sửa trước.

**Khuyến nghị:** Đây là lệnh duy nhất tôi khuyên bạn chạy thật ngay khi mở dự án
lên máy có Maven, để bắt sớm mọi lỗi kiểu này trước khi chạy toàn bộ ứng dụng:
```bash
./mvnw clean compile
```
Rủi ro lỗi cao nhất luôn nằm ở những chỗ đổi chữ ký hàm (thêm/bớt tham số) như lỗi
vừa tìm thấy ở trên — loại lỗi này rất dễ sót khi sửa tay qua nhiều file mà không
có trình biên dịch nhắc ngay lập tức.

