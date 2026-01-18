/**
 * 회사 정보 조회 응답 DTO
 * 사용 목적 :
 * - 회사 단건 조회 결과 반환
 * - Company Entity 데이터를 외부 응답용으로 변환
 */
package io.why503.accountservice.domain.companies.model.dto.res;

import io.why503.accountservice.domain.companies.model.dto.CompanyBank;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class CompanyResDto {

    private Long companySq;          // 회사 식별자
    private CompanyBank companyBank; // 회사 정산 은행
    private String accountNumber;          // 회사 정산 계좌 번호

    private String companyName;      // 회사명
    private String ownerName;        // 대표자명
    private String companyPhone;     // 회사 연락처
    private String companyEmail;     // 회사 대표 이메일
    private String companyAddr;      // 회사 주소
    private String companyPost;      // 회사 우편번호

    private Long amount;             // 회사 정산 금액
    private LocalDateTime amountDate; // 정산 금액 등록/변경 시각

    /**
     * Company Entity → CompanyResDto 변환 생성자
     *
     * 설계 의도 :
     * - Entity 직접 노출 방지
     * - 응답 데이터 구조를 DTO로 명확히 분리
     */
    @Builder
    public CompanyResDto(
            Long companySq,         CompanyBank companyBank,
            String accountNumber,   String companyName,
            String ownerName,       String companyPhone,
            String companyEmail,    String companyAddr,
            String companyPost,     Long amount,
            LocalDateTime amountDate) {
        this.companySq = companySq;
        this.companyBank = companyBank;
        this.accountNumber = accountNumber;
        this.companyName = companyName;
        this.ownerName = ownerName;
        this.companyPhone = companyPhone;
        this.companyEmail = companyEmail;
        this.companyAddr = companyAddr;
        this.companyPost = companyPost;
        this.amount = amount;
        this.amountDate = amountDate;
    }
}
