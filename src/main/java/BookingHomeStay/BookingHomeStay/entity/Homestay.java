package BookingHomeStay.BookingHomeStay.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "homestays")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Homestay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private Host host;

    @Column(nullable = false, unique = true, length = 255)
    private String slug;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 100)
    private String province;

    private String district;

    @Column(nullable = false, length = 255)
    private String address;

    private Double latitude;
    private Double longitude;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private HomestayStatus status = HomestayStatus.PENDING;

    @Builder.Default
    @OneToMany(mappedBy = "homestay", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HomestayImage> images = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "homestay", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HomestayTranslation> translations = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "homestay", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Room> rooms = new ArrayList<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(
        name = "homestay_amenities",
        joinColumns = @JoinColumn(name = "homestay_id"),
        inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    private Set<Amenity> amenities = new HashSet<>();

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public String getDescriptionFor(String locale) {
        return translations.stream()
                .filter(t -> t.getLocale().equalsIgnoreCase(locale))
                .findFirst()
                .map(HomestayTranslation::getDescription)
                .orElse(this.description);
    }

    public String getNameFor(String locale) {
        return translations.stream()
                .filter(t -> t.getLocale().equalsIgnoreCase(locale))
                .findFirst()
                .map(HomestayTranslation::getName)
                .orElse(this.name);
    }
}