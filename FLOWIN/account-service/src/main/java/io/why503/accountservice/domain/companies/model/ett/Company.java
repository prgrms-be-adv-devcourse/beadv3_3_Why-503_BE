/**
 * Company Entity
 * 사용 목적 :
 * - 회사 기본 정보 및 정산 정보를 DB에 영속화
 */

package io.why503.accountservice.domain.companies.model.ett;

import io.why503.accountservice.domain.accounts.model.entity.Account;
import io.why503.accountservice.domain.companies.model.dto.CompanyBank;
import io.why503.accountservice.domain.companies.model.dto.cmd.CompanyCmd;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "company")
@Getter
@NoArgsConstructor
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_sq")
    private Long companySq;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_sq",
            nullable = false,
            unique = true
    )
    private Account owner;

    @Enumerated(EnumType.STRING)
    @Column(name = "company_bank", nullable = false)
    private CompanyBank companyBank;

    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(name = "company_phone", nullable = false)
    private String companyPhone;

    @Column(name = "company_email", nullable = false)
    private String companyEmail;

    @Column(name = "company_addr", nullable = false)
    private String companyAddr;

    @Column(name = "company_post", nullable = false)
    private String companyPost;

    @Column(name = "amount", nullable = false)
    private Long amount = 0L;

    @Column(name = "amount_date", nullable = false)
    private LocalDateTime amountDate;

    public Company(Account owner, CompanyCmd cmd) {
        this.owner = owner;
        this.companyBank = cmd.getCompanyBank();
        this.accountNumber = cmd.getAccountNumber();
        this.companyName = cmd.getCompanyName();
        this.ownerName = cmd.getOwnerName();
        this.companyPhone = cmd.getCompanyPhone();
        this.companyEmail = cmd.getCompanyEmail();
        this.companyAddr = cmd.getCompanyAddr();
        this.companyPost = cmd.getCompanyPost();
        this.amountDate = LocalDateTime.now();
    }
    public void increaseAmount(Long increase){
        this.amount += increase;
    }
    public void decreaseAmount(Long decrease){
        this.amount -= decrease;
    }
}
