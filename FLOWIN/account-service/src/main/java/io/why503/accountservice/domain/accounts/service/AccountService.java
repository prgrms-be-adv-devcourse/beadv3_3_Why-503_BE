package io.why503.accountservice.domain.accounts.service;


import io.why503.accountservice.domain.accounts.model.dto.response.UserSummaryResponse;
import io.why503.accountservice.domain.accounts.model.enums.UserRole;
import io.why503.accountservice.domain.accounts.model.dto.requests.UpsertAccountRequest;
import io.why503.accountservice.domain.accounts.model.entity.Account;

import java.util.List;
/*
account service 인터페이스
 */
public interface AccountService {
    UserSummaryResponse create(UpsertAccountRequest request);
    List<UserSummaryResponse> readAll();
    UserSummaryResponse readBySq(Long sq);
    UserSummaryResponse readById(String id);
    UserRole readUserRoleBySq(Long sq);
    UserSummaryResponse updateBySq(Long sq, UpsertAccountRequest request);
    UserSummaryResponse updateById(String id, UpsertAccountRequest request);
    UserSummaryResponse updateUserRoleBySq(Long sq, UserRole role);
    UserSummaryResponse deleteBySq(Long sq);
    UserSummaryResponse deleteById(String id);
    boolean existId(String id);
}
