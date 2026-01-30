package io.why503.accountservice.domain.auth.model.dto;

import io.why503.accountservice.domain.accounts.model.enums.UserRole;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/*
jwt payload에 쓰는 클래스
 */
@Getter
@RequiredArgsConstructor
@Builder
public class AccountDetails implements UserDetails {
    private final String username;
    private final String password;
    //위까지가 기본 UserDetails에 들어가는 변수
    private final Long sq;
    private final UserRole role;
    //여기까지가 더 넣어줄 변수

    //인증 payload에 넣을 권한 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }
}
