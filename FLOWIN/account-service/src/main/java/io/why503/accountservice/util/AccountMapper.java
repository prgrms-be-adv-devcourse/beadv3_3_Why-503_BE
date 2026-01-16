package io.why503.accountservice.util;


import io.why503.accountservice.domain.accounts.model.dto.response.UserSummaryResponse;
import io.why503.accountservice.domain.accounts.model.enums.Gender;
import io.why503.accountservice.domain.auth.model.dto.AccountDetails;
import io.why503.accountservice.domain.accounts.model.dto.vo.UpsertAccountVo;
import io.why503.accountservice.domain.accounts.model.dto.requests.UpsertAccountRequest;
import io.why503.accountservice.domain.accounts.model.entity.Account;
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
    public UpsertAccountVo upsertDtoToUpsertVo(UpsertAccountRequest dto) {
        return new UpsertAccountVo(
                dto.id(),
                dto.password(),
                dto.name(),
                dto.birthday(),
                Gender.getGender(dto.gender()),
                dto.phone(),
                dto.email(),
                dto.basicAddr(),
                dto.detailAddr(),
                dto.post()
        );
    }
    //엔티티를 찾아서 Detail로 만들기 위한 함수, payload에 사용함
    public AccountDetails entityToDetail(Account account){
        return new AccountDetails(
                account.getId(),
                account.getPassword(),
                account.getSq(),
                account.getRole());
    }
    //엔티티를 찾아서 summaryResponse로 만들기 위한 함수
    public UserSummaryResponse entityToSummaryResponse(Account account){
        return new UserSummaryResponse(
                account.getSq(),
                account.getName(),
                account.getRole());
    }
}
