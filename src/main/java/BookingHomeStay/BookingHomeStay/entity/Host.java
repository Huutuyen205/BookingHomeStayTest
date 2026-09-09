package BookingHomeStay.BookingHomeStay.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "hosts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Host {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, length = 150)
    private String businessName;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private HostStatus status = HostStatus.PENDING;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}