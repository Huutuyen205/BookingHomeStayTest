package BookingHomeStay.BookingHomeStay.service;

import BookingHomeStay.BookingHomeStay.dto.HomestayForm;
import BookingHomeStay.BookingHomeStay.entity.Homestay;
import java.math.BigDecimal;
import java.util.List;

public interface HomestayService {
    List<Homestay> search(String province, String district, Integer guests, BigDecimal maxPrice);
    List<String> getDistrictsOfProvince(String province);
    List<Homestay> findNearby(double lat, double lng, double radiusKm);
    Homestay getActiveBySlug(String slug);
    Homestay getByIdForHost(Long id, Long hostUserId);
    List<Homestay> getMyHomestays(Long hostUserId);
    Homestay createHomestay(HomestayForm form, Long hostUserId);
    Homestay updateHomestay(HomestayForm form, Long hostUserId);
    void toggleActive(Long homestayId, Long hostUserId);
    List<Homestay> getPendingHomestays();
    void approve(Long homestayId);
    void reject(Long homestayId);
    List<Homestay> getAll();
}