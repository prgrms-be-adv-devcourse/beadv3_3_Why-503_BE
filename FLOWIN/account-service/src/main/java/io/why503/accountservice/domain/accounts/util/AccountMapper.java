package io.why503.accountservice.domain.accounts.util;


import io.why503.accountservice.domain.accounts.model.dto.response.UserCompanyResponse;
import io.why503.accountservice.domain.accounts.model.dto.response.UserPointResponse;
import io.why503.accountservice.domain.accounts.model.dto.response.UserRoleResponse;
import io.why503.accountservice.domain.auth.model.dto.AccountDetails;
import io.why503.accountservice.domain.accounts.model.dto.requests.CreateAccountRequest;
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
    public Account upsertRequestToEntity(CreateAccountRequest request) {
        return Account.builder()
                .id(request.userId())
                .password(passwordEncoder.encode(request.userPassword()))
                .name(request.userName())
                .birthday(request.birthday())
                .gender(request.gender())
                .phone(request.userPhone())
                .email(request.userEmail())
                .basicAddr(request.userBasicAddr())
                .detailAddr(request.userDetailAddr())
                .post(request.userPost())
                .build();
    }
    //엔티티를 찾아서 Detail로 만들기 위한 함수, payload에 사용함
    public AccountDetails entityToDetail(Account account){
        return new AccountDetails(
                account.getId(),
                account.getPassword(),
                account.getSq(),
                account.getRole()
        );
    }
    //엔티티를 찾아서 roleResponse로 만들기 위한 함수
    public UserRoleResponse entityToRoleResponse(Account account){
        return new UserRoleResponse(
                account.getSq(),
                account.getName(),
                account.getRole()
        );
    }
    //엔티티를 찾아서 PointResponse로 만들기 위한 함수
    public UserPointResponse entityToPointResponse(Account account){
        return new UserPointResponse(
                account.getPoint()
        );
    }
    //엔티티를 찾아서 CompanyResponse로 만들기 위한 함수
    public UserCompanyResponse entityToCompanyResponse(Account account){
        return new UserCompanyResponse(
                account.getCompany().getSq()
        );
    }

}
