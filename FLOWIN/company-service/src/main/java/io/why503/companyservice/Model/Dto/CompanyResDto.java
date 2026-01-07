// 조회

package io.why503.companyservice.Model.Dto;

import lombok.Getter;
import java.time.LocalDateTime;
import io.why503.companyservice.Model.Ett.Company;
import io.why503.companyservice.Model.Ett.Enum.CompanyBank;

@Getter
public class CompanyResDto {

    private Long companySq;
    // private Long userSq;

    private CompanyBank companyBank;
    private String account;
    private String companyName;
    private String ownerName;
    private String companyPhone;
    private String companyEmail;
    private String companyAddr;
    private String companyPost;

    private Long amount;
    private LocalDateTime amountDate;

    public CompanyResDto(Company company) {
        this.companySq = company.getCompanySq();
        // this.userSq = company.getUserSq();
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
