package BookingHomeStay.BookingHomeStay.controller;

import BookingHomeStay.BookingHomeStay.dto.PromotionForm;
import BookingHomeStay.BookingHomeStay.dto.RoomForm;
import BookingHomeStay.BookingHomeStay.service.AdminService;
import BookingHomeStay.BookingHomeStay.service.HomestayService;
import BookingHomeStay.BookingHomeStay.service.BookingService;
import BookingHomeStay.BookingHomeStay.service.PromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Toan bo chuc nang danh cho Quan tri vien (ADMIN).
 * Duong dan goc: /quan-tri  (truoc day la /admin, da doi sang tieng Viet).
 *
 * LUU Y: ten route (vd "/chu-nha") va ten FILE template tra ve (vd
 * "quan-tri/chu-nha-cho-duyet") la 2 thu khac nhau, khong bat buoc phai trung
 * chu - route la duong dan tren trinh duyet, view name la duong dan file .html.
 */
@Controller
@RequestMapping("/quan-tri")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final HomestayService homestayService;
    private final BookingService bookingService;
    private final PromotionService promotionService;

    // ================= TONG QUAN =================

    // Trang tong quan chinh cua Admin: 4 the so lieu (nguoi dung/chu nha/homestay/
    // don cho xu ly) + bieu do tron nguon don hang + bieu do cot theo thang.
    @GetMapping("/tong-quan")
    public String dashboard(Model model) {
        model.addAttribute("stats", adminService.getDashboardStats());
        model.addAttribute("bookingsByMonth", adminService.getBookingCountByMonth());
        model.addAttribute("bookingsBySource", adminService.getBookingCountBySource());
        return "quan-tri/tong-quan";
    }

    // ================= NGUOI DUNG =================

    // Hien danh sach TOAN BO nguoi dung, chia thanh 4 nhom: Admin / Chu nha /
    // Cong tac vien / Khach hang (task 9 - phan biet 3 moi quan he nghiep vu).
    @GetMapping("/nguoi-dung")
    public String nguoiDung(Model model) {
        model.addAttribute("nguoiDungTheoVaiTro", adminService.getUsersGroupedByRole());
        return "quan-tri/nguoi-dung";
    }

    // Khoa hoac mo khoa 1 tai khoan (bam 1 nut thi doi trang thai nguoc lai).
    // Tai khoan bi khoa (BLOCKED) se KHONG DANG NHAP DUOC nua ngay lap tuc.
    @PostMapping("/nguoi-dung/{id}/khoa-mo")
    public String toggleUserLock(@PathVariable Long id) {
        adminService.toggleUserLock(id);
        return "redirect:/quan-tri/nguoi-dung";
    }

    // ================= DUYET CHU NHA =================

    // Danh sach cac Chu nha dang cho Admin duyet (trang thai PENDING).
    @GetMapping("/chu-nha")
    public String hosts(Model model) {
        model.addAttribute("hosts", adminService.getPendingHosts());
        return "quan-tri/chu-nha-cho-duyet";
    }

    // Duyet 1 Chu nha -> chuyen trang thai sang APPROVED, tu do ho moi dang duoc homestay.
    @PostMapping("/chu-nha/{id}/duyet")
    public String approveHost(@PathVariable Long id) {
        adminService.approveHost(id);
        return "redirect:/quan-tri/chu-nha";
    }

    // Tu choi 1 yeu cau lam Chu nha -> chuyen trang thai sang REJECTED.
    @PostMapping("/chu-nha/{id}/tu-choi")
    public String rejectHost(@PathVariable Long id) {
        adminService.rejectHost(id);
        return "redirect:/quan-tri/chu-nha";
    }

    // ================= DUYET CONG TAC VIEN (task 6) =================

    // Danh sach dang ky lam Cong tac vien dang cho duyet. Tach RIENG khoi trang
    // duyet Chu nha o tren de Admin de dang phan biet 2 vai tro khac nhau.
    @GetMapping("/cong-tac-vien")
    public String collaborators(Model model) {
        model.addAttribute("collaborators", adminService.getPendingCollaborators());
        return "quan-tri/cong-tac-vien-cho-duyet";
    }

    // Xac nhan day THAT SU la 1 Cong tac vien hop le -> ma gioi thieu cua ho
    // (maGioiThieu) tu do co the dung de tinh hoa hong khi khach dat phong.
    @PostMapping("/cong-tac-vien/{id}/duyet")
    public String approveCollaborator(@PathVariable Long id) {
        adminService.approveCollaborator(id);
        return "redirect:/quan-tri/cong-tac-vien";
    }

    // Tu choi yeu cau lam Cong tac vien.
    @PostMapping("/cong-tac-vien/{id}/tu-choi")
    public String rejectCollaborator(@PathVariable Long id) {
        adminService.rejectCollaborator(id);
        return "redirect:/quan-tri/cong-tac-vien";
    }

    // ================= DUYET HOMESTAY =================

    // Danh sach TOAN BO homestay (moi trang thai) de Admin xem va duyet.
    @GetMapping("/homestay")
    public String homestays(Model model) {
        model.addAttribute("homestays", homestayService.getAll());
        return "quan-tri/homestay-cho-duyet";
    }

    // Duyet 1 homestay -> chuyen sang ACTIVE, tu do khach moi tim thay va dat duoc.
    @PostMapping("/homestay/{id}/duyet")
    public String approveHomestay(@PathVariable Long id) {
        homestayService.approve(id);
        return "redirect:/quan-tri/homestay";
    }

    // Tu choi 1 homestay.
    @PostMapping("/homestay/{id}/tu-choi")
    public String rejectHomestay(@PathVariable Long id) {
        homestayService.reject(id);
        return "redirect:/quan-tri/homestay";
    }

    // ================= CRUD PHONG (task 1, 2, 5) =================

    // Danh sach TOAN BO phong trong he thong, khong phan biet cua Chu nha nao
    // (khac voi trang "/chu-nha/homestay/{id}/phong" chi thay phong CUA RIENG minh).
    @GetMapping("/phong")
    public String rooms(Model model) {
        model.addAttribute("rooms", adminService.getAllRoomsForAdmin());
        return "quan-tri/phong";
    }

    // Mo form sua 1 phong BAT KY (khong kiem tra Admin co phai chu phong khong -
    // day la diem KHAC BIET voi form sua phong cua Chu nha).
    @GetMapping("/phong/{id}/sua")
    public String editRoomForm(@PathVariable Long id, Model model) {
        var room = adminService.getRoomForAdmin(id);
        RoomForm form = new RoomForm();
        form.setId(room.getId());
        form.setName(room.getName());
        form.setPricePerNight(room.getPricePerNight());
        form.setMaxGuests(room.getMaxGuests());
        form.setDescription(room.getDescription());
        model.addAttribute("room", room);
        model.addAttribute("roomForm", form);
        return "quan-tri/phong-form";
    }

    // Luu thay doi (chu yeu la GIA - task 5) cho 1 phong bat ky.
    @PostMapping("/phong/luu")
    public String saveRoom(@Valid @ModelAttribute RoomForm form, BindingResult result) {
        if (result.hasErrors()) return "quan-tri/phong-form";
        adminService.adminUpdateRoom(form);
        return "redirect:/quan-tri/phong";
    }

    // Bat/tat trang thai "Con phong / Het phong" cho 1 phong bat ky (task 17).
    @PostMapping("/phong/{id}/doi-trang-thai")
    public String toggleRoomStatus(@PathVariable Long id) {
        adminService.adminToggleRoomStatus(id);
        return "redirect:/quan-tri/phong";
    }

    // ================= CRUD MA GIAM GIA (task 3) =================

    // Danh sach toan bo ma giam gia (ca dang ACTIVE lan da khoa INACTIVE).
    @GetMapping("/ma-giam-gia")
    public String promotions(Model model) {
        model.addAttribute("promotions", promotionService.getAll());
        return "quan-tri/ma-giam-gia";
    }

    // Mo form trong de tao 1 ma giam gia moi.
    @GetMapping("/ma-giam-gia/them-moi")
    public String newPromotionForm(Model model) {
        model.addAttribute("promotionForm", new PromotionForm());
        return "quan-tri/ma-giam-gia-form";
    }

    // Mo form sua, dien san du lieu cua ma giam gia da co (theo id).
    @GetMapping("/ma-giam-gia/{id}/sua")
    public String editPromotionForm(@PathVariable Long id, Model model) {
        var promo = promotionService.getById(id);
        PromotionForm form = new PromotionForm();
        form.setId(promo.getId());
        form.setCode(promo.getCode());
        form.setDiscountType(promo.getDiscountType());
        form.setDiscountValue(promo.getDiscountValue());
        form.setMaxDiscountAmount(promo.getMaxDiscountAmount());
        form.setMinOrderAmount(promo.getMinOrderAmount());
        form.setStartDate(promo.getStartDate());
        form.setEndDate(promo.getEndDate());
        form.setUsageLimit(promo.getUsageLimit());
        model.addAttribute("promotionForm", form);
        return "quan-tri/ma-giam-gia-form";
    }

    // Luu ma giam gia: neu form.id = null thi TAO MOI, nguoc lai thi CAP NHAT
    // (logic phan biet nam trong PromotionServiceImpl.save()).
    @PostMapping("/ma-giam-gia/luu")
    public String savePromotion(@Valid @ModelAttribute PromotionForm form, BindingResult result) {
        if (result.hasErrors()) return "quan-tri/ma-giam-gia-form";
        promotionService.save(form);
        return "redirect:/quan-tri/ma-giam-gia";
    }

    // Khoa (ACTIVE -> INACTIVE) hoac mo lai 1 ma giam gia. Dung cach nay thay vi
    // xoa han khi ma DA TUNG duoc su dung, de khong lam mat du lieu lich su don hang.
    @PostMapping("/ma-giam-gia/{id}/doi-trang-thai")
    public String togglePromotionStatus(@PathVariable Long id) {
        promotionService.toggleStatus(id);
        return "redirect:/quan-tri/ma-giam-gia";
    }

    // Xoa VINH VIEN 1 ma giam gia - CHI thanh cong neu ma nay CHUA TUNG duoc
    // dung cho don hang nao (xem PromotionServiceImpl.delete() de biet ly do).
    // Neu da dung roi se bao loi va goi y dung nut "Khoa" thay vi "Xoa".
    @PostMapping("/ma-giam-gia/{id}/xoa")
    public String deletePromotion(@PathVariable Long id, Model model) {
        try {
            promotionService.delete(id);
        } catch (IllegalStateException e) {
            model.addAttribute("promotions", promotionService.getAll());
            model.addAttribute("errorMessage", e.getMessage());
            return "quan-tri/ma-giam-gia";
        }
        return "redirect:/quan-tri/ma-giam-gia";
    }

    // ================= DON DAT PHONG =================

    // Xem TOAN BO don dat phong trong he thong (moi Chu nha, moi trang thai).
    @GetMapping("/don-dat-phong")
    public String bookings(Model model) {
        model.addAttribute("bookings", bookingService.getAllBookings());
        return "quan-tri/don-dat-phong";
    }
}
