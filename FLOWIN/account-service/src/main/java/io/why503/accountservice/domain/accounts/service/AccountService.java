package io.why503.accountservice.domain.accounts.service;


import io.why503.accountservice.domain.accounts.model.dto.response.UserCompanyResponse;
import io.why503.accountservice.domain.accounts.model.dto.response.UserPointResponse;
import io.why503.accountservice.domain.accounts.model.dto.response.UserRoleResponse;
import io.why503.accountservice.domain.accounts.model.enums.UserRole;
import io.why503.accountservice.domain.accounts.model.dto.requests.UpsertAccountRequest;

import java.util.List;
/*
account service 인터페이스
 */
public interface AccountService {
    UserRoleResponse create(UpsertAccountRequest request);
    List<UserRoleResponse> readAll();
    UserRoleResponse readBySq(Long sq);
    UserRoleResponse readById(String id);
    UserRole readUserRoleBySq(Long sq);
    UserRoleResponse updateBySq(Long sq, UpsertAccountRequest request);
    UserRoleResponse updateById(String id, UpsertAccountRequest request);
    UserRoleResponse updateUserRoleBySq(Long sq, UserRole role);
    UserRoleResponse deleteBySq(Long sq);
    UserRoleResponse deleteById(String id);
    boolean existId(String id);
    UserPointResponse readPointBySq(Long sq);
    UserCompanyResponse readCompanyBySq(Long sq);
}
