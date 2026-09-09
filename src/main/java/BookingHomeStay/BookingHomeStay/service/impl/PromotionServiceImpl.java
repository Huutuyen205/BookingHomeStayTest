package BookingHomeStay.BookingHomeStay.service.impl;

import BookingHomeStay.BookingHomeStay.dto.PromotionForm;
import BookingHomeStay.BookingHomeStay.entity.DiscountType;
import BookingHomeStay.BookingHomeStay.entity.Promotion;
import BookingHomeStay.BookingHomeStay.exception.ResourceNotFoundException;
import BookingHomeStay.BookingHomeStay.repository.BookingRepository;
import BookingHomeStay.BookingHomeStay.repository.PromotionRepository;
import BookingHomeStay.BookingHomeStay.service.PromotionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final BookingRepository bookingRepository;

    // Kiem tra 1 ma giam gia co HOP LE de ap dung khong: phai dang ACTIVE, con
    // trong thoi han (startDate..endDate), chua het luot dung, va don hang phai
    // du gia tri toi thieu (minOrderAmount). Neu sai bat ky dieu kien nao se
    // nem loi IllegalArgumentException voi thong bao ro rang cho khach hang doc.
    @Override
    public Promotion validate(String code, BigDecimal orderAmount) {
        Promotion promo = promotionRepository.findByCodeAndStatus(code, "ACTIVE")
                .orElseThrow(() -> new IllegalArgumentException("Ma giam gia khong hop le"));

        LocalDate now = LocalDate.now();
        if (now.isBefore(promo.getStartDate()) || now.isAfter(promo.getEndDate())) {
            throw new IllegalArgumentException("Ma giam gia da het han");
        }
        if (promo.getUsageLimit() > 0 && promo.getUsedCount() >= promo.getUsageLimit()) {
            throw new IllegalArgumentException("Ma giam gia da het luot su dung");
        }
        if (orderAmount.compareTo(promo.getMinOrderAmount()) < 0) {
            throw new IllegalArgumentException("Don hang chua dat gia tri toi thieu de ap ma");
        }
        return promo;
    }

    // Tinh so tien duoc giam: neu loai PHAN TRAM thi = don_hang * gia_tri / 100
    // (co the bi gioi han boi maxDiscountAmount neu co thiet lap), neu loai
    // SO TIEN CO DINH thi giam dung bang gia tri do. Sau khi tinh xong, TANG
    // usedCount len 1 de theo doi so lan da su dung ma nay.
    @Override
    @Transactional
    public BigDecimal calculateDiscount(Promotion promo, BigDecimal orderAmount) {
        BigDecimal discount = promo.getDiscountType() == DiscountType.PERCENTAGE
                ? orderAmount.multiply(promo.getDiscountValue()).divide(BigDecimal.valueOf(100))
                : promo.getDiscountValue();

        if (promo.getMaxDiscountAmount() != null && discount.compareTo(promo.getMaxDiscountAmount()) > 0) {
            discount = promo.getMaxDiscountAmount();
        }
        promo.setUsedCount(promo.getUsedCount() + 1);
        promotionRepository.save(promo);
        return discount;
    }

    // ===== CAC HAM DUOI DAY PHUC VU CRUD CHO ADMIN (task 3) =====

    // Lay toan bo ma giam gia (moi trang thai) de hien o trang danh sach.
    @Override
    public List<Promotion> getAll() {
        return promotionRepository.findAll();
    }

    // Lay 1 ma giam gia theo id, nem loi ro rang neu khong tim thay.
    @Override
    public Promotion getById(Long id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay ma giam gia id=" + id));
    }

    // Luu 1 ma giam gia: neu form KHONG co id -> TAO MOI (mac dinh ACTIVE, da
    // dung 0 lan); neu form CO id -> tim ban ghi cu roi CAP NHAT de tren cung
    // 1 id, tranh tao trung ma giam gia.
    @Override
    @Transactional
    public Promotion save(PromotionForm form) {
        Promotion promo = form.getId() != null ? getById(form.getId()) : Promotion.builder().status("ACTIVE").usedCount(0).build();
        promo.setCode(form.getCode().trim().toUpperCase());
        promo.setDiscountType(form.getDiscountType());
        promo.setDiscountValue(form.getDiscountValue());
        promo.setMaxDiscountAmount(form.getMaxDiscountAmount());
        promo.setMinOrderAmount(form.getMinOrderAmount() != null ? form.getMinOrderAmount() : BigDecimal.ZERO);
        promo.setStartDate(form.getStartDate());
        promo.setEndDate(form.getEndDate());
        promo.setUsageLimit(form.getUsageLimit() != null ? form.getUsageLimit() : 0);
        return promotionRepository.save(promo);
    }

    // Dao trang thai ACTIVE <-> INACTIVE. Dung de "khoa tam thoi" 1 ma ma
    // KHONG xoa du lieu, an toan hon delete() khi ma da tung duoc su dung.
    @Override
    @Transactional
    public void toggleStatus(Long id) {
        Promotion promo = getById(id);
        promo.setStatus("ACTIVE".equals(promo.getStatus()) ? "INACTIVE" : "ACTIVE");
        promotionRepository.save(promo);
    }

    // Xoa VINH VIEN 1 ma giam gia khoi database. CHI cho phep xoa neu ma nay
    // CHUA TUNG duoc gan vao don hang nao (kiem tra qua bookingRepository).
    // Ly do: bang "bookings" co khoa ngoai promotion_id tro toi bang nay - neu
    // xoa ma da dung roi se vi pham rang buoc khoa ngoai (foreign key) hoac lam
    // "mo coi" du lieu lich su don hang cu. Neu roi vao truong hop nay, Admin
    // nen dung toggleStatus() (nut "Khoa") thay vi xoa.
    @Override
    @Transactional
    public void delete(Long id) {
        if (bookingRepository.existsByPromotionId(id)) {
            throw new IllegalStateException("Ma nay da duoc su dung trong don hang, chi co the Khoa chu khong the xoa.");
        }
        promotionRepository.deleteById(id);
    }
}
