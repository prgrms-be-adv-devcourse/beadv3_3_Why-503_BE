package io.why503.accountservice.domain.accounts.model.entity;

import io.why503.accountservice.common.model.entity.BasicEntity;
import io.why503.accountservice.domain.accounts.model.enums.Gender;
import io.why503.accountservice.domain.accounts.model.enums.UserRole;
import io.why503.accountservice.domain.accounts.model.enums.UserStatus;
import io.why503.accountservice.domain.companies.model.entity.Company;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/*
개체 jpa랑 sql을 이어주는 역할,
ddl-auto = validate, 즉 검증만 하고 테이블을 만들거나 건들지 않음
 */
@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends BasicEntity {
    @Column(name = "id")
    private String id;          //유니크함 = 비공식 식별자

    @Column(name = "password")
    private String password;

    @Setter
    @Column(name = "birthday")
    private LocalDateTime birthday;

    @Setter
    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "join_date")
    private final LocalDateTime joinDate = LocalDateTime.now();

    @Column(name = "withdrawal_date")
    private LocalDateTime withdrawDate; //dbDefault = LocalDateTime.of(9999, 12, 31, 23, 59, 59);

    @Setter
    @Column(name = "agree_date")
    private LocalDateTime agreeDate = LocalDateTime.now();

    @Setter
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private UserStatus stat = UserStatus.NORMAL;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "company_sq", nullable = true)
    private Company company;

    @Column(name = "point")
    private Long point = 0L;

    //생성자, 암호화는 이미 mapper에서 호출 되었음
    @Builder
    public Account(
            String id,
            String password,
            String name,
            LocalDateTime birthday,
            Gender gender,
            String phone,
            String email,
            String basicAddr,
            String detailAddr,
            String post) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.birthday = birthday;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.basicAddr = basicAddr;
        this.detailAddr = detailAddr;
        this.post = post;
        this.withdrawDate = LocalDateTime.of(9999, 12, 31, 23, 59, 59);
    }
    //포인트 증가, 후에 포인트(예치금) 계산을 위해 생성
    public void increasePoint(Long increase){
        this.point += increase;
    }
    //포인트 감소
    public void decreasePoint(Long decrease){

        this.point -= decrease;
    }
    public void joinCompany(Company company, UserRole role){
        this.company = company;
        this.role = role;
    }
    public void leaveCompany(){
        this.company = null;
    }
    //탈퇴
    public void withdraw(){
        withdrawDate = LocalDateTime.now();
        this.stat = UserStatus.WITHDRAW;
    }
}
