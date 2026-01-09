package io.why503.accountservice.account.repo;

import io.why503.accountservice.account.model.ett.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepo extends JpaRepository<Account, Long> {
    Optional<Account> findBySq(Long sq);
    Optional<Account> findById(String id);
}
