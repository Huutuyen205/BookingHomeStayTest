package BookingHomeStay.BookingHomeStay.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "amenities")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Amenity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    private String icon;
}