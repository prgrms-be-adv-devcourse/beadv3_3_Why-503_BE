package io.why503.accountservice.domain.auth.service.impl;


import io.why503.accountservice.domain.auth.model.dto.AccountDetails;
import io.why503.accountservice.domain.accounts.util.AccountMapper;
import io.why503.accountservice.domain.accounts.model.entity.Account;
import io.why503.accountservice.domain.accounts.repository.AccountJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
/*
jwt필터에서 사용, 받은 id로 테이블을 조회해서 AccountDetail을 반환
 */
@Service
@RequiredArgsConstructor
public class AccountDetailsSvImpl implements UserDetailsService {
    private final AccountJpaRepository accountJpaRepository;
    private final AccountMapper accountMapper;
    @Override
    public AccountDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        Account account = accountJpaRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("id = " + id + " Account is not found")
        );
        return accountMapper.entityToDetail(account);
    }
    public AccountDetails loadUserBySq(Long sq) throws UsernameNotFoundException {
        Account account = accountJpaRepository.findBySq(sq).orElseThrow(
                () -> new IllegalArgumentException("id = " + sq + " Account is not found")
        );
        return accountMapper.entityToDetail(account);
    }
}
