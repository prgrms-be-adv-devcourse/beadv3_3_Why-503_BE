package io.why503.accountservice.Mapper;


import io.why503.accountservice.Model.Dto.UpsertAccountCmd;
import io.why503.accountservice.Model.Dto.UpsertAccountReq;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountMapper {
    private final PasswordEncoder passwordEncoder;

    public UpsertAccountCmd upsertDtoToUpsertCmd(UpsertAccountReq dto) {
        return UpsertAccountCmd.builder()
                .id(dto.id())                   .password(passwordEncoder.encode(dto.password()))
                .name(dto.name())               .birthday(dto.birthday())
                .gender(dto.gender())           .phone(dto.phone())
                .email(dto.email())             .basicAddr(dto.basicAddr())
                .detailAddr(dto.detailAddr())   .post(dto.post())
                .build();
    }
}
