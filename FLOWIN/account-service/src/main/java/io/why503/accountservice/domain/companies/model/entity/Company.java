/**
 * Company Entity
 * 사용 목적 :
 * - 회사 기본 정보 및 정산 정보를 DB에 영속화
 */

package io.why503.accountservice.domain.companies.model.entity;

import io.why503.accountservice.common.model.entity.BasicEntity;
import io.why503.accountservice.domain.companies.model.enums.CompanyBank;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "company")
@Getter
@NoArgsConstructor
public class Company extends BasicEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "bank", nullable = false)
    private CompanyBank bank;

    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(name = "amount", nullable = false)
    private Long amount = 0L;

    @Column(name = "amount_date", nullable = false)
    private LocalDateTime amountDate;

    @Builder
    public Company(
            CompanyBank bank,
            String accountNumber,
            String name,
            String ownerName,
            String phone,
            String email,
            String basicAddr,
            String detailAddr,
            String post,
            LocalDateTime amountDate
    ){
        this.bank = bank;
        this.accountNumber = accountNumber;
        this.name = name;
        this.ownerName = ownerName;
        this.phone = phone;
        this.email = email;
        this.basicAddr = basicAddr;
        this.detailAddr = detailAddr;
        this.post = post;
        this.amountDate = amountDate;
    }

    public void increaseAmount(Long increase){
        this.amount += increase;
    }
    public void decreaseAmount(Long decrease){
        this.amount -= decrease;
    }
}
