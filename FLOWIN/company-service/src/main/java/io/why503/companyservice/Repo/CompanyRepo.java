// CompanyRequest.java

package io.why503.companyservice.Repo;

import io.why503.companyservice.Model.Ett.Company;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepo extends JpaRepository<Company, Long> {
    // Optional<Company> findByUserSq(Long userSq);

    // boolean existsByUserSq(Long userSq);

}
