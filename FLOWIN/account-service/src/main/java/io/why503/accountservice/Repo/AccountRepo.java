package io.why503.accountservice.Repo;

import io.why503.accountservice.Model.Ett.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepo extends JpaRepository<Account, Long> {
    Optional<Account> findBySq(Long sq);
    Optional<Account> findById(String id);
}
