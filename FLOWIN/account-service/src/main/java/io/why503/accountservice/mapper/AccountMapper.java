package io.why503.accountservice.mapper;


import io.why503.accountservice.domain.auth.model.dto.AccountDetails;
import io.why503.accountservice.domain.account.model.dto.UpsertAccountCmd;
import io.why503.accountservice.domain.account.model.dto.UpsertAccountReq;
import io.why503.accountservice.domain.account.model.ett.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/*
여러 dto를 다른 dto로 만들어주는 스위치역할
추가할 가능성 있음
 */
@Component
@RequiredArgsConstructor
public class AccountMapper {
    private final PasswordEncoder passwordEncoder;
    //받은 dto의 password를 bcrypt해서 엔티티를 생성, 수정할 때 쓰는 cmd클래스로 만들기 위한 함수
    public UpsertAccountCmd upsertDtoToUpsertCmd(UpsertAccountReq dto) {
        return UpsertAccountCmd.builder()
                .id(dto.id())                   .password(passwordEncoder.encode(dto.password()))
                .name(dto.name())               .birthday(dto.birthday())
                .gender(dto.gender())           .phone(dto.phone())
                .email(dto.email())             .basicAddr(dto.basicAddr())
                .detailAddr(dto.detailAddr())   .post(dto.post())
                .build();
    }
    //엔티티를 찾아서 Detail로 만들기 위한 함수, payload에 사용함
    public AccountDetails EttToDetail(Account account){
        return new AccountDetails(
                account.getId(),
                account.getPassword(),
                account.getSq(),
                account.getName(),
                account.getRole());
    }
}
