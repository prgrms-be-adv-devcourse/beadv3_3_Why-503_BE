package io.why503.performanceservice.domain.hall.model.entity;

import io.why503.performanceservice.domain.hall.model.dto.enums.HallStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "hall")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HallEntity {

    // 공연장 식별자 (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sq")
    private Long sq;

    // 공연장명
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    // 공연장 우편번호
    @Column(name = "post", nullable = false, length = 8)
    private String post;

    // 공연장 기본 주소
    @Column(name = "basic_addr", nullable = false, length = 255)
    private String basicAddr;

    // 공연장 상세 주소
    @Column(name = "detail_addr", nullable = false, length = 255)
    private String detailAddr;

    // 공연장 상태 (Enum)
    @Column(name = "status", nullable = false, length = 1)
    private String status;

    // 공연장 총 좌석 수
    @Column(name = "seat_scale", nullable = false)
    private Integer seatScale;

    // 공연장 구조 정보 (예: 단층, 2층, 원형 등)
    @Column(name = "structure", nullable = false, length = 50)
    private String structure;

    // 공연장 위도
    @Column(name = "latitude", nullable = false, precision = 10, scale = 8)
    private BigDecimal latitude;

    // 공연장 경도
    @Column(name = "longitude", nullable = false, precision = 11, scale = 8)
    private BigDecimal longitude;

    @Builder
    public HallEntity(
            String name,
            String post,
            String basicAddr,
            String detailAddr,
            String status,
            Integer seatScale,
            String structure,
            BigDecimal latitude,
            BigDecimal longitude) {
        this.name = name;
        this.post = post;
        this.basicAddr = basicAddr;
        this.detailAddr = detailAddr;
        this.status = status;
        this.seatScale = seatScale;
        this.structure = structure;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    // 콘서트 상태 코드 -> Enum 반환
    public HallStatus getHallStatus() {
        return HallStatus.fromCode(this.status);
    }

    // 콘서트 Enum -> 코드 값 저장
    public void setHallStatus(HallStatus status) {
        this.status = status.getCode();
    }
}