package BookingHomeStay.BookingHomeStay.repository;

import BookingHomeStay.BookingHomeStay.entity.Homestay;
import BookingHomeStay.BookingHomeStay.entity.HomestayStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface HomestayRepository extends JpaRepository<Homestay, Long> {

    List<Homestay> findByHostId(Long hostId);

    List<Homestay> findByStatus(HomestayStatus status);

    Optional<Homestay> findBySlugAndStatus(String slug, HomestayStatus status);

    @Query("""
        SELECT DISTINCT h FROM Homestay h JOIN h.rooms r
        WHERE h.status = BookingHomeStay.BookingHomeStay.entity.HomestayStatus.ACTIVE
          AND (:province IS NULL OR LOWER(h.province) LIKE LOWER(CONCAT('%', :province, '%')))
          AND (:district IS NULL OR LOWER(h.district) LIKE LOWER(CONCAT('%', :district, '%')))
          AND (:minGuests IS NULL OR r.maxGuests >= :minGuests)
          AND (:maxPrice IS NULL OR r.pricePerNight <= :maxPrice)
        """)
    List<Homestay> search(@Param("province") String province,
                           @Param("district") String district,
                           @Param("minGuests") Integer minGuests,
                           @Param("maxPrice") BigDecimal maxPrice);

    // Task 12: lay danh sach quan/huyen CO THAT (da co homestay dang hoat dong)
    // trong 1 tinh, dung de do dropdown "Quan/huyen" khi khach da chon 1 Diem den
    // pho bien - khong con phai chon lai tinh/thanh nua.
    @Query("""
        SELECT DISTINCT h.district FROM Homestay h
        WHERE h.status = BookingHomeStay.BookingHomeStay.entity.HomestayStatus.ACTIVE
          AND LOWER(h.province) LIKE LOWER(CONCAT('%', :province, '%'))
          AND h.district IS NOT NULL
        ORDER BY h.district
        """)
    List<String> findDistinctDistrictsByProvince(@Param("province") String province);

    /** Cong thuc Haversine - tim homestay ACTIVE trong ban kinh radiusKm quanh toa do khach */
    @Query(value = """
        SELECT h.*,
          ( 6371 * acos(
              cos(radians(:lat)) * cos(radians(h.latitude)) *
              cos(radians(h.longitude) - radians(:lng)) +
              sin(radians(:lat)) * sin(radians(h.latitude))
          ) ) AS distance_km
        FROM homestays h
        WHERE h.status = 'ACTIVE' AND h.latitude IS NOT NULL AND h.longitude IS NOT NULL
        HAVING distance_km <= :radiusKm
        ORDER BY distance_km ASC
        LIMIT 20
        """, nativeQuery = true)
    List<Homestay> findNearby(@Param("lat") double lat,
                               @Param("lng") double lng,
                               @Param("radiusKm") double radiusKm);
}