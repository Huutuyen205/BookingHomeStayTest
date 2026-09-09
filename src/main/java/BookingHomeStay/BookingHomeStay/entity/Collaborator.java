package BookingHomeStay.BookingHomeStay.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Cong tac vien / Sale: nguoi gioi thieu khach, ban phong ho Chu nha de huong
 * hoa hong, KHONG dong nghia voi Chu nha (khong tu dang homestay) va cung
 * KHONG phai Khach hang thong thuong (task 8 - can phan biet 3 nhom quan he).
 */
@Entity
@Table(name = "collaborators")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Collaborator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String maGioiThieu; // ma CTV rieng, dung khi chia se link cho khach

    private String soTaiKhoanNhanHoaHong;
    private String tenNganHang;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CollaboratorStatus status = CollaboratorStatus.PENDING;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
