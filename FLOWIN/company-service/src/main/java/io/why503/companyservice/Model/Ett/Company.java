// Company.java

package io.why503.companyservice.Model.Ett;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import io.why503.companyservice.Model.Ett.Enum.CompanyBank;

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

    // @Column(name = "user_sq", nullable = false)
    // private Long userSq;

    @Enumerated(EnumType.STRING)
    @Column(name = "company_bank", nullable = false)
    private CompanyBank companyBank;

    @Column(name = "account", nullable = false)
    private String account;

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
    private Long amount;

    @Column(name = "amount_date", nullable = false)
    private LocalDateTime amountDate;

    /* 생성 메서드 */
    public static Company create(
            // Long userSq,
            CompanyBank companyBank,
            String account,
            String companyName,
            String ownerName,
            String companyPhone,
            String companyEmail,
            String companyAddr,
            String companyPost,
            Long amount
    ) {
        Company company = new Company();
        company.companyBank = companyBank;
        company.account = account;
        company.companyName = companyName;
        company.ownerName = ownerName;
        company.companyPhone = companyPhone;
        company.companyEmail = companyEmail;
        company.companyAddr = companyAddr;
        company.companyPost = companyPost;
        company.amount = amount;
        company.amountDate = LocalDateTime.now();

        return company;
    }
}
