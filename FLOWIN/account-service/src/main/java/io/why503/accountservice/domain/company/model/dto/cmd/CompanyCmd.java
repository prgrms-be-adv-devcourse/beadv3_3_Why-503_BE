package io.why503.accountservice.domain.company.model.dto.cmd;

import io.why503.accountservice.domain.company.model.dto.CompanyBank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CompanyCmd {
    private CompanyBank companyBank;
    private String accountNumber;
    private String companyName;
    private String ownerName;
    private String companyPhone;
    private String companyEmail;
    private String companyAddr;
    private String companyPost;
    @Builder
    public CompanyCmd(
            //Long userSq,
            CompanyBank companyBank,
            String accountNumber,
            String companyName,
            String ownerName,
            String companyPhone,
            String companyEmail,
            String companyAddr,
            String companyPost
    ) {
        this.companyBank = companyBank;
        this.accountNumber = accountNumber;
        this.companyName = companyName;
        this.ownerName = ownerName;
        this.companyPhone = companyPhone;
        this.companyEmail = companyEmail;
        this.companyAddr = companyAddr;
        this.companyPost = companyPost;
    }
}
