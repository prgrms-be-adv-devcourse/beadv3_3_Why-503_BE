package io.why503.reservationservice.Domain.Concert.Model.Ett; // 패키지명 본인 프로젝트에 맞게 확인!

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "seat_class")
public class SeatClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_class_sq")
    private Long seatClassSq;

    @Column(name = "show_sq")
    private Long showSq;

    @Column(name = "seat_class")
    private String seatClass; // 등급명 (SS, S, R)

    @Column(name = "seat_price")
    private Integer seatPrice; // ★ 이 필드가 있어야 .getSeatPrice() 에러가 사라짐!
}