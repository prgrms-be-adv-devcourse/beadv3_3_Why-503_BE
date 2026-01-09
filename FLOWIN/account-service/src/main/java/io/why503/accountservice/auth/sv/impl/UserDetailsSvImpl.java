package io.why503.accountservice.auth.sv.impl;


import io.why503.accountservice.mapper.AccountMapper;
import io.why503.accountservice.account.model.ett.Account;
import io.why503.accountservice.account.repo.AccountRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsSvImpl implements UserDetailsService {
    private final AccountRepo accountRepo;
    private final AccountMapper accountMapper;
    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        Account account = accountRepo.findById(id).orElseThrow(
                () -> new IllegalArgumentException("id = " + id + " Account is not found")
        );
        return accountMapper.EttToDetail(account);
    }
}
