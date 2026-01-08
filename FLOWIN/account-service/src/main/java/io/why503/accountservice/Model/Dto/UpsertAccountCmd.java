package io.why503.accountservice.Model.Dto;

import io.why503.accountservice.Model.Enum.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
