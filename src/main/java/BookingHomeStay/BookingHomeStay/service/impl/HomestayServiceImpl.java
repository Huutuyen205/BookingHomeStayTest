package BookingHomeStay.BookingHomeStay.service.impl;

import BookingHomeStay.BookingHomeStay.dto.HomestayForm;
import BookingHomeStay.BookingHomeStay.entity.*;
import BookingHomeStay.BookingHomeStay.exception.OwnershipException;
import BookingHomeStay.BookingHomeStay.exception.ResourceNotFoundException;
import BookingHomeStay.BookingHomeStay.repository.HomestayRepository;
import BookingHomeStay.BookingHomeStay.repository.HostRepository;
import BookingHomeStay.BookingHomeStay.service.HomestayService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HomestayServiceImpl implements HomestayService {

    private final HomestayRepository homestayRepository;
    private final HostRepository hostRepository;

    @Override
    public List<Homestay> search(String province, String district, Integer guests, BigDecimal maxPrice) {
        return homestayRepository.search(province, district, guests, maxPrice);
    }

    @Override
    public List<String> getDistrictsOfProvince(String province) {
        return homestayRepository.findDistinctDistrictsByProvince(province);
    }

    @Override
    public List<Homestay> findNearby(double lat, double lng, double radiusKm) {
        return homestayRepository.findNearby(lat, lng, radiusKm > 0 ? radiusKm : 15.0);
    }

    @Override
    public Homestay getActiveBySlug(String slug) {
        return homestayRepository.findBySlugAndStatus(slug, HomestayStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay homestay: " + slug));
    }

    @Override
    public Homestay getByIdForHost(Long id, Long hostUserId) {
        Homestay homestay = homestayRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay homestay id=" + id));
        assertOwnership(homestay, hostUserId);
        return homestay;
    }

    @Override
    public List<Homestay> getMyHomestays(Long hostUserId) {
        Host host = getHostOrThrow(hostUserId);
        return homestayRepository.findByHostId(host.getId());
    }

    @Override
    @Transactional
    public Homestay createHomestay(HomestayForm form, Long hostUserId) {
        Host host = getHostOrThrow(hostUserId);
        if (host.getStatus() != HostStatus.APPROVED) {
            throw new OwnershipException("Tai khoan Host chua duoc duyet, chua the tao homestay");
        }
        String slug = toSlug(form.getName()) + "-" + System.currentTimeMillis() % 10000;

        Homestay homestay = Homestay.builder()
                .host(host)
                .slug(slug)
                .name(form.getName())
                .description(form.getDescription())
                .province(form.getProvince())
                .district(form.getDistrict())
                .address(form.getAddress())
                .latitude(form.getLatitude())
                .longitude(form.getLongitude())
                .status(HomestayStatus.PENDING)
                .build();
        return homestayRepository.save(homestay);
    }

    @Override
    @Transactional
    public Homestay updateHomestay(HomestayForm form, Long hostUserId) {
        Homestay homestay = getByIdForHost(form.getId(), hostUserId);
        homestay.setName(form.getName());
        homestay.setDescription(form.getDescription());
        homestay.setProvince(form.getProvince());
        homestay.setDistrict(form.getDistrict());
        homestay.setAddress(form.getAddress());
        homestay.setLatitude(form.getLatitude());
        homestay.setLongitude(form.getLongitude());
        homestay.setStatus(HomestayStatus.PENDING);
        return homestayRepository.save(homestay);
    }

    @Override
    @Transactional
    public void toggleActive(Long homestayId, Long hostUserId) {
        Homestay homestay = getByIdForHost(homestayId, hostUserId);
        homestay.setStatus(homestay.getStatus() == HomestayStatus.ACTIVE
                ? HomestayStatus.INACTIVE : HomestayStatus.ACTIVE);
        homestayRepository.save(homestay);
    }

    @Override
    public List<Homestay> getPendingHomestays() {
        return homestayRepository.findByStatus(HomestayStatus.PENDING);
    }

    @Override
    @Transactional
    public void approve(Long homestayId) {
        Homestay h = findOrThrow(homestayId);
        h.setStatus(HomestayStatus.ACTIVE);
        homestayRepository.save(h);
    }

    @Override
    @Transactional
    public void reject(Long homestayId) {
        Homestay h = findOrThrow(homestayId);
        h.setStatus(HomestayStatus.REJECTED);
        homestayRepository.save(h);
    }

    @Override
    public List<Homestay> getAll() {
        return homestayRepository.findAll();
    }

    private Homestay findOrThrow(Long id) {
        return homestayRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay homestay id=" + id));
    }

    private Host getHostOrThrow(Long userId) {
        return hostRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Tai khoan chua dang ky lam Host"));
    }

    private void assertOwnership(Homestay homestay, Long hostUserId) {
        if (!homestay.getHost().getUser().getId().equals(hostUserId)) {
            throw new OwnershipException("Ban khong co quyen thao tac tren homestay nay");
        }
    }

    private String toSlug(String input) {
        String noAccent = java.text.Normalizer.normalize(input, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}", "");
        return noAccent.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
    }
}