package io.why503.accountservice.domain.account.model.dto.cmd;

import io.why503.accountservice.domain.account.model.dto.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/*
Req -> Ett로 갈 때, password암호화를 위해서 사용,
암호화는 bean특성상 다른 곳에 선언
사용은 mapper에서 사용
 */
@Getter
@NoArgsConstructor
public class UpsertAccountCmd {
    @NotBlank private String id;
    @NotBlank private String password;
    @NotBlank private String name;
    @NotNull private LocalDateTime birthday;
    @NotNull private Gender gender;
    @NotBlank private String phone;
    @NotBlank private String email;
    @NotBlank private String basicAddr;
    @NotBlank private String detailAddr;
    @NotBlank private String post;

    @Builder
    public UpsertAccountCmd(
            String id,              String password,
            String name,            LocalDateTime birthday,
            Gender gender,          String phone,
            String email,           String basicAddr,
            String detailAddr,      String post)
    {
        this.id = id;                   this.password = password;
        this.name = name;               this.birthday = birthday;
        this.gender = gender;           this.phone = phone;
        this.email = email;             this.basicAddr = basicAddr;
        this.detailAddr = detailAddr;   this.post = post;
    }
}
