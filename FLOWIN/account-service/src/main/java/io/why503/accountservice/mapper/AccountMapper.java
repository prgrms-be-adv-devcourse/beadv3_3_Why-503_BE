package io.why503.accountservice.mapper;


import io.why503.accountservice.auth.model.dto.AccountDetails;
import io.why503.accountservice.account.model.dto.UpsertAccountCmd;
import io.why503.accountservice.account.model.dto.UpsertAccountReq;
import io.why503.accountservice.account.model.ett.Account;
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

    public AccountDetails EttToDetail(Account account){
        return new AccountDetails(
                account.getId(),
                account.getPassword(),
                account.getSq(),
                account.getName(),
                account.getRole());
    }
}
