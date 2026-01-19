package io.why503.accountservice.domain.accounts.model.entity;


import io.why503.accountservice.domain.accounts.model.dto.vo.UpsertAccountVo;
import io.why503.accountservice.domain.accounts.model.enums.Gender;
import io.why503.accountservice.domain.accounts.model.enums.UserRole;
import io.why503.accountservice.domain.accounts.model.enums.UserStatus;
import io.why503.accountservice.domain.accounts.utils.converter.GenderConverter;
import io.why503.accountservice.domain.accounts.utils.converter.UserRoleConverter;
import io.why503.accountservice.domain.accounts.utils.converter.UserStatusConverter;
import io.why503.accountservice.domain.companies.model.entitys.Company;
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
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sq")
    private Long sq;

    @Column(name = "id")
    private String id;          //유니크함 = 비공식 식별자

    @Column(name = "password")
    private String password;

    @Setter
    @Column(name = "name")
    private String name;

    @Setter
    @Column(name = "birthday")
    private LocalDateTime birthday;

    @Setter
    @Column(name = "gender")
    @Convert(converter = GenderConverter.class)
    private Gender gender;

    @Setter
    @Column(name = "phone")
    private String phone;

    @Setter
    @Column(name = "email")
    private String email;

    @Setter
    @Column(name = "basic_addr")
    private String basicAddr;

    @Setter
    @Column(name = "detail_addr")
    private String detailAddr;

    @Setter
    @Column(name = "post")
    private String post;

    @Column(name = "join_date")
    private final LocalDateTime joinDate = LocalDateTime.now();

    @Column(name = "withdrawal_date")
    private LocalDateTime withdrawDate; //dbDefault = LocalDateTime.of(9999, 12, 31, 23, 59, 59);

    @Setter
    @Column(name = "agree_date")
    private LocalDateTime agreeDate = LocalDateTime.now();

    @Setter
    @Column(name = "role")
    @Convert(converter = UserRoleConverter.class)
    private UserRole role = UserRole.USER;

    @Column(name = "status")
    @Convert(converter = UserStatusConverter.class)
    private UserStatus stat = UserStatus.NORMAL;

    @OneToOne(mappedBy = "owner", fetch = FetchType.LAZY)
    private Company company;

    @Column(name = "point")
    private Long point = 0L;
    //생성자, 암호화는 이미 cmd에서 완료
    public Account(UpsertAccountVo vo){
        this.id = vo.userId();
        this.password = vo.userPassword();
        this.name = vo.userName();
        this.birthday = vo.birthday();
        this.gender = vo.gender();
        this.phone = vo.userPhone();
        this.email = vo.userEmail();
        this.basicAddr = vo.userBasicAddr();
        this.detailAddr = vo.userDetailAddr();
        this.post = vo.userPost();
        withdrawDate = LocalDateTime.of(9999, 12, 31, 23, 59, 59);
    }
    //수정
    public void update(UpsertAccountVo vo){
        this.id = vo.userId();
        this.password = vo.userPassword();
        this.name = vo.userName();
        this.birthday = vo.birthday();
        this.gender = vo.gender();
        this.phone = vo.userPhone();
        this.email = vo.userEmail();
        this.basicAddr = vo.userBasicAddr();
        this.detailAddr = vo.userDetailAddr();
        this.post = vo.userPost();
    }
    //포인트 증가, 후에 포인트(예치금) 계산을 위해 생성
    public void increasePoint(Long increase){
        this.point += increase;
    }
    //포인트 감소
    public void decreasePoint(Long decrease){
        this.point -= decrease;
    }
    /*
    role 변경, Admin만 사용가능,
    이 함수는 이 개체의 Role을 변경하는 것이 아니라 target의 role을 변경
     */
    public void grantRole(Account target, UserRole role) throws Exception{
        if(this.role != UserRole.ADMIN){
            //throw new AccessDeniedException("only, Admin can change user's Role");
            throw new Exception("only, Admin can change user's Role");
        }
         target.setRole(role);
    }
    //탈퇴
    public void withdraw(){
        withdrawDate = LocalDateTime.now();
        this.stat = UserStatus.WITHDRAW;
    }
}
