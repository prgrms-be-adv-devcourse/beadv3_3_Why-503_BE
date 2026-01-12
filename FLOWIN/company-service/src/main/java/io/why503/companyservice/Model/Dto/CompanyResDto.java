/**
 * 회사 정보 조회 응답 DTO
 *
 * 사용 목적 :
 * - 회사 단건 조회 결과 반환
 * - Company Entity 데이터를 외부 응답용으로 변환
 */
package io.why503.companyservice.Model.Dto;

import lombok.Getter;
import java.time.LocalDateTime;
import io.why503.companyservice.Model.Ett.Company;
import io.why503.companyservice.Model.Ett.Enum.CompanyBank;

@Getter
public class CompanyResDto {

    private Long companySq;          // 회사 식별자
    private CompanyBank companyBank; // 회사 정산 은행
    private String account;          // 회사 정산 계좌 번호

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
    public CompanyResDto(Company company) {
        this.companySq = company.getCompanySq();
        this.companyBank = company.getCompanyBank();
        this.account = company.getAccount();
        this.companyName = company.getCompanyName();
        this.ownerName = company.getOwnerName();
        this.companyPhone = company.getCompanyPhone();
        this.companyEmail = company.getCompanyEmail();
        this.companyAddr = company.getCompanyAddr();
        this.companyPost = company.getCompanyPost();
        this.amount = company.getAmount();
        this.amountDate = company.getAmountDate();
    }
}
