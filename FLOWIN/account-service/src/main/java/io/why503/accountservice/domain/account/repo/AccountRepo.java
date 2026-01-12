package io.why503.accountservice.domain.account.repo;

import io.why503.accountservice.domain.account.model.ett.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
/*
레포지터리
 */
public interface AccountRepo extends JpaRepository<Account, Long> {
    Optional<Account> findBySq(Long sq);
    Optional<Account> findById(String id);
}
