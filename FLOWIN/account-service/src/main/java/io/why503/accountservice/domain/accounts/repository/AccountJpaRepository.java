package io.why503.accountservice.domain.accounts.repository;

import io.why503.accountservice.domain.accounts.model.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
/*
레포지터리
 */
public interface AccountJpaRepository extends JpaRepository<Account, Long> {
    Optional<Account> findBySq(Long sq);
    Optional<Account> findById(String id);
}
