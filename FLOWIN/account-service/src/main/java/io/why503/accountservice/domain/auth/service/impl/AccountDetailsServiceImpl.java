package io.why503.accountservice.domain.auth.service.impl;

import io.why503.accountservice.domain.accounts.service.AccountService;
import io.why503.accountservice.domain.auth.model.dto.AccountDetails;
import io.why503.accountservice.domain.accounts.util.AccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
/*
jwt필터에서 사용, 받은 id로 테이블을 조회해서 AccountDetail을 반환
 */
@Service
@RequiredArgsConstructor
public class AccountDetailsServiceImpl implements UserDetailsService {
    private final AccountService accountService;
    private final AccountMapper accountMapper;
    @Override
    public AccountDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        return accountMapper.entityToDetail(accountService.findById(id));
    }
    public AccountDetails loadUserBySq(Long sq) throws UsernameNotFoundException {
        return accountMapper.entityToDetail(accountService.findBySq(sq));
    }
}
