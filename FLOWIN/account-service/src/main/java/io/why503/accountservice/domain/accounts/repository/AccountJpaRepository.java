package io.why503.accountservice.domain.accounts.repository;

import io.why503.accountservice.domain.accounts.model.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
/*
레포지터리
 */
public interface AccountJpaRepository extends JpaRepository<Account, Long> {
    Optional<Account> findBySq(Long sq);
    Optional<Account> findById(String id);
    boolean existsById(String id);
    List<Account> findByCompany_Sq(Long companySq);
}
