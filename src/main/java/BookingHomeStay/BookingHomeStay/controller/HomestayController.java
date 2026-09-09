package BookingHomeStay.BookingHomeStay.controller;

import BookingHomeStay.BookingHomeStay.entity.Homestay;
import BookingHomeStay.BookingHomeStay.service.HomestayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class HomestayController {

    private final HomestayService homestayService;

    @GetMapping("/homestay/{slug}")
    public String detail(@PathVariable String slug, Model model) {
        Homestay homestay = homestayService.getActiveBySlug(slug);
        model.addAttribute("homestay", homestay);
        return "chi-tiet-homestay";
    }

    @GetMapping("/homestay/gan-day")
    public String nearbyPage() { return "homestay-gan-day"; }

    @GetMapping("/homestay/api/gan-day")
    @ResponseBody
    public List<Map<String, Object>> nearbyApi(@RequestParam double lat, @RequestParam double lng) {
        return homestayService.findNearby(lat, lng, 15).stream()
                .map(h -> Map.<String, Object>of(
                        "slug", h.getSlug(), "name", h.getName(), "province", h.getProvince()))
                .collect(Collectors.toList());
    }
}