package io.why503.accountservice.Account.Model.Ett;


import io.why503.accountservice.Account.Model.Dto.UpsertAccountCmd;
import io.why503.accountservice.Account.Model.Enum.Gender;
import io.why503.accountservice.Account.Model.Enum.UserRole;
import io.why503.accountservice.Account.Model.Enum.UserStat;
import io.why503.accountservice.Account.Model.Enum.EnumConverter.GenderConverter;
import io.why503.accountservice.Account.Model.Enum.EnumConverter.UserRoleConverter;
import io.why503.accountservice.Account.Model.Enum.EnumConverter.UserStatConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_sq")
    private Long sq;

    @Column(name = "user_id")
    private String id;          //유니크함 = 비공식 식별자

    @Column(name = "user_password")
    private String password;

    @Setter
    @Column(name = "user_name")
    private String name;

    @Setter
    @Column(name = "user_birthday")
    private LocalDateTime birthday;

    @Setter
    @Column(name = "gender")
    @Convert(converter = GenderConverter.class)
    private Gender gender;

    @Setter
    @Column(name = "user_phone")
    private String phone;

    @Setter
    @Column(name = "user_email")
    private String email;

    @Setter
    @Column(name = "user_basic_addr")
    private String basicAddr;

    @Setter
    @Column(name = "user_detail_addr")
    private String detailAddr;

    @Setter
    @Column(name = "user_post")
    private String post;

    @Column(name = "user_join_date")
    private final LocalDateTime joinDate = LocalDateTime.now();

    @Column(name = "user_withdrawal_date")
    private LocalDateTime withdrawDate; //dbDefault = LocalDateTime.of(9999, 12, 31, 23, 59, 59);

    @Setter
    @Column(name = "agree_date")
    private LocalDateTime agreeDate = LocalDateTime.now();

    @Setter
    @Column(name = "user_role")
    @Convert(converter = UserRoleConverter.class)
    private UserRole role = UserRole.USER;

    @Column(name = "user_stat")
    @Convert(converter = UserStatConverter.class)
    private UserStat stat = UserStat.NORMAL;

    @Column(name = "user_point")
    private Long point = 0L;

    @Builder
    public Account(
            String id,              String password,
            String name,            LocalDateTime birthday,
            Gender gender,          String phone,
            String email,           String basicAddr,
            String detailAddr,      String post){
        this.id = id;                   this.password = password;
        this.name = name;               this.birthday = birthday;
        this.gender = gender;           this.phone = phone;
        this.email = email;             this.basicAddr = basicAddr;
        this.detailAddr = detailAddr;   this.post = post;
        withdrawDate = LocalDateTime.of(9999, 12, 31, 23, 59, 59);
    }

    public Account(UpsertAccountCmd cmd){
        this.id = cmd.getId();                  this.password = cmd.getPassword();
        this.name = cmd.getName();              this.birthday = cmd.getBirthday();
        this.gender = cmd.getGender();          this.phone = cmd.getPhone();
        this.email = cmd.getEmail();            this.basicAddr = cmd.getBasicAddr();
        this.detailAddr = cmd.getDetailAddr();  this.post = cmd.getPost();
        withdrawDate = LocalDateTime.of(9999, 12, 31, 23, 59, 59);
    }

    public void update(
            String id,              String password,
            String name,            LocalDateTime birthday,
            Gender gender,          String phone,
            String email,           String basicAddr,
            String detailAddr,      String post) {
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
    }
    public void update(UpsertAccountCmd cmd){
        this.id = cmd.getId();                  this.password = cmd.getPassword();
        this.name = cmd.getName();              this.birthday = cmd.getBirthday();
        this.gender = cmd.getGender();          this.phone = cmd.getPhone();
        this.email = cmd.getEmail();            this.basicAddr = cmd.getBasicAddr();
        this.detailAddr = cmd.getDetailAddr();  this.post = cmd.getPost();
    }

    public void increasePoint(Long increase){
        this.point += increase;
    }
    public void decreasePoint(Long decrease){
        this.point -= decrease;
    }

    public void grantRole(Account target, UserRole role) throws Exception{
        if(this.role != UserRole.ADMIN){
            //throw new AccessDeniedException("only, Admin can change user's Role");
            throw new Exception("only, Admin can change user's Role");
        }
         target.setRole(role);
    }
    public void withdraw(){
        withdrawDate = LocalDateTime.now();
        this.stat = UserStat.WITHDRAW;
    }
}
