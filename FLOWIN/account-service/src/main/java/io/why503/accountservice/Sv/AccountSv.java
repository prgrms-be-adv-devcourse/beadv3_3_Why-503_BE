package io.why503.accountservice.Sv;


import io.why503.accountservice.Model.Dto.UpsertAccountDto;
import io.why503.accountservice.Model.Ett.Account;
import io.why503.accountservice.Repo.AccountRepo;

import java.util.List;

public interface AccountSv {
    public Account create(UpsertAccountDto request);
    public List<Account> readAll();
    public Account readBySq(Long sq);
    public Account readById(String id);
    public Account update(String id, UpsertAccountDto request);
    public Account delete(String id);
    public boolean existId(String id);
}
