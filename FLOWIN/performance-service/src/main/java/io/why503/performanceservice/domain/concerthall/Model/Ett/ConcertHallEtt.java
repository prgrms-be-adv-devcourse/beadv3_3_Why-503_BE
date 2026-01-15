/**
 * Concert Hall Entity
 * 공연장(concert_hall) 테이블과 매핑되는 JPA Entity
 *
 * 사용 목적 :
 * - 공연 등록 시 참조되는 공연장 기본 정보 관리
 * - 공연(show) 도메인의 FK 기준 테이블
 *
 * 설계 메모 :
 * - 공연장은 공연보다 선행 생성되는 데이터
 * - 좌석(seat), 공연(show) 등 다른 도메인에서 참조됨
 */
package io.why503.performanceservice.domain.concerthall.Model.Ett;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "concert_hall")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ConcertHallEtt {

    /**
     * 공연장 식별자 (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concert_hall_sq")
    private Long concertHallSq;

    /**
     * 공연장명
     */
    @Column(name = "concert_hall_name", nullable = false, length = 100)
    private String concertHallName;

    /**
     * 공연장 우편번호
     */
    @Column(name = "concert_hall_post", nullable = false, length = 8)
    private String concertHallPost;

    /**
     * 공연장 기본 주소
     */
    @Column(name = "concert_hall_basic_addr", nullable = false, length = 255)
    private String concertHallBasicAddr;

    /**
     * 공연장 상세 주소
     */
    @Column(name = "concert_hall_detail_addr", nullable = false, length = 255)
    private String concertHallDetailAddr;

    /**
     * 공연장 상태
     */
    @Column(name = "concert_hall_stat", nullable = false, length = 1)
    private String concertHallStat;

    /**
     * 공연장 총 좌석 수
     */
    @Column(name = "concert_hall_seat_scale", nullable = false)
    private Integer concertHallSeatScale;

    /**
     * 공연장 구조 정보
     * (예: 단층, 2층, 원형 등)
     */
    @Column(name = "concert_hall_structure", nullable = false, length = 50)
    private String concertHallStructure;

    /**
     * 공연장 위도
     */
    @Column(name = "concert_hall_latitude", nullable = false, precision = 10, scale = 8)
    private BigDecimal concertHallLatitude;

    /**
     * 공연장 경도
     */
    @Column(name = "concert_hall_longitude", nullable = false, precision = 11, scale = 8)
    private BigDecimal concertHallLongitude;
}
