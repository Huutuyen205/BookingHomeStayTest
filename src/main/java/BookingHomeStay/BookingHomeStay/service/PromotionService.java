package BookingHomeStay.BookingHomeStay.service;

import BookingHomeStay.BookingHomeStay.dto.PromotionForm;
import BookingHomeStay.BookingHomeStay.entity.Promotion;
import java.math.BigDecimal;
import java.util.List;

public interface PromotionService {
    Promotion validate(String code, BigDecimal orderAmount);
    BigDecimal calculateDiscount(Promotion promotion, BigDecimal orderAmount);

    // ===== CRUD danh cho Admin (task 3) =====
    List<Promotion> getAll();
    Promotion getById(Long id);
    Promotion save(PromotionForm form);
    void toggleStatus(Long id); // ACTIVE <-> INACTIVE ("khoa" ma, khong xoa cung)
    void delete(Long id);       // chi xoa cung neu ma chua tung duoc dung cho don nao
}
