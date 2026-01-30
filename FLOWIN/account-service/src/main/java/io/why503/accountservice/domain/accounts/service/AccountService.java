package io.why503.accountservice.domain.accounts.service;


import io.why503.accountservice.domain.accounts.model.enums.UserRole;
import io.why503.accountservice.domain.accounts.model.dto.requests.CreateAccountRequest;
import io.why503.accountservice.domain.accounts.model.dto.response.UserCompanyResponse;
import io.why503.accountservice.domain.accounts.model.dto.response.UserPointResponse;
import io.why503.accountservice.domain.accounts.model.dto.response.UserRoleResponse;
import io.why503.accountservice.domain.companies.model.entity.Company;

import java.util.List;
/*
account service 인터페이스
 */
public interface AccountService {
    List<UserRoleResponse> readAll();
    List<UserRoleResponse> readCompanyMember(Long companySq);

    UserRoleResponse create(CreateAccountRequest request);
    UserRoleResponse readBySq(Long sq);
    UserRoleResponse readById(String id);
    UserRoleResponse deleteBySq(Long sq);
    UserRoleResponse deleteById(String id);
    UserRoleResponse increasePoint(Long sq, Long point);
    UserRoleResponse decreasePoint(Long sq, Long point);
    UserRoleResponse joinCompany(Long userSq , Company company, UserRole role);
    UserRoleResponse leaveCompany(Long userSq);

    UserCompanyResponse readCompanyBySq(Long sq);

    UserPointResponse readPointBySq(Long sq);

    UserRole readUserRoleBySq(Long sq);

    void grantCompany(Long sq);

    boolean existId(String id);
}
