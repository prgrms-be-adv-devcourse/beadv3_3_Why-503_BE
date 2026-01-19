/**
 * Company Entity
 * 사용 목적 :
 * - 회사 기본 정보 및 정산 정보를 DB에 영속화
 */

package io.why503.accountservice.domain.companies.model.entitys;

import io.why503.accountservice.domain.accounts.model.entity.Account;
import io.why503.accountservice.domain.companies.model.enums.CompanyBank;
import io.why503.accountservice.domain.companies.model.dto.vo.CompanyVo;
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
    @Column(name = "sq")
    private Long sq;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_sq",
            nullable = false,
            unique = true
    )
    private Account owner;

    @Enumerated(EnumType.STRING)
    @Column(name = "bank", nullable = false)
    private CompanyBank bank;

    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "basic_addr", nullable = false)
    private String basicAddr;

    @Column(name = "detail_addr", nullable = false)
    private String detailAddr;

    @Column(name = "post", nullable = false)
    private String post;

    @Column(name = "amount", nullable = false)
    private Long amount = 0L;

    @Column(name = "amount_date", nullable = false)
    private LocalDateTime amountDate;

    public Company(Account owner, CompanyVo vo) {
        this.owner = owner;
        this.bank = vo.companyBank();
        this.accountNumber = vo.accountNumber();
        this.name = vo.companyName();
        this.ownerName = vo.ownerName();
        this.phone = vo.companyPhone();
        this.email = vo.companyEmail();
        this.basicAddr = vo.companyBasicAddr();
        this.detailAddr = vo.companyDetailAddr();
        this.post = vo.companyPost();
        this.amountDate = LocalDateTime.now();      //교체 예정
    }
    public void increaseAmount(Long increase){
        this.amount += increase;
    }
    public void decreaseAmount(Long decrease){
        this.amount -= decrease;
    }
}
