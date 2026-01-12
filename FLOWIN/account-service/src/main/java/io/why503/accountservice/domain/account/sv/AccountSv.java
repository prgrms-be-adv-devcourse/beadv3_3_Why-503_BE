package io.why503.accountservice.domain.account.sv;


import io.why503.accountservice.domain.account.model.dto.UpsertAccountReq;
import io.why503.accountservice.domain.account.model.ett.Account;

import java.util.List;
/*
account service 인터페이스
 */
public interface AccountSv {
    public Account create(UpsertAccountReq request);
    public List<Account> readAll();
    public Account readBySq(Long sq);
    public Account readById(String id);
    public Account updateBySq(Long sq, UpsertAccountReq request);
    public Account updateById(String id, UpsertAccountReq request);
    public Account deleteBySq(Long sq);
    public Account deleteById(String id);
    public boolean existId(String id);
}
