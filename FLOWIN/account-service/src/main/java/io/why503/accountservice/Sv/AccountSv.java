package io.why503.accountservice.Sv;


import io.why503.accountservice.Model.Dto.UpsertAccountReq;
import io.why503.accountservice.Model.Ett.Account;

import java.util.List;

public interface AccountSv {
    public Account create(UpsertAccountReq request);
    public List<Account> readAll();
    public Account readBySq(Long sq);
    public Account readById(String id);
    public Account update(String id, UpsertAccountReq request);
    public Account delete(String id);
    public boolean existId(String id);
}
