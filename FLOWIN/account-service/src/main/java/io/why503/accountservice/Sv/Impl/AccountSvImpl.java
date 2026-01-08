package io.why503.accountservice.Sv.Impl;

import io.why503.accountservice.Mapper.AccountMapper;
import io.why503.accountservice.Model.Dto.UpsertAccountReq;
import io.why503.accountservice.Model.Ett.Account;
import io.why503.accountservice.Repo.AccountRepo;
import io.why503.accountservice.Sv.AccountSv;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountSvImpl implements AccountSv {
    private final AccountRepo accountRepo;
    private final AccountMapper accountMapper;
    @Override
    @Transactional
    public Account create(UpsertAccountReq request){

        Account account = new Account(accountMapper.upsertDtoToUpsertCmd(request));
        return accountRepo.save(account);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> readAll() {
        return accountRepo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Account readBySq(Long sq) {
        return accountRepo.findBySq(sq).orElseThrow(
                () -> new IllegalArgumentException("sq = " + sq + " Account is not found")
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Account readById(String id) {
        return accountRepo.findById(id).orElseThrow(
                () -> new IllegalArgumentException("id = " + id + " Account is not found")
        );
    }

    @Override
    @Transactional
    public Account update(String id, UpsertAccountReq request) {
        Account account = readById(id);
        account.update(accountMapper.upsertDtoToUpsertCmd(request));
        return account;
    }

    @Override
    @Transactional
    public Account delete(String id) {
        Account account = readById(id);
        accountRepo.delete(account);
        return account;
    }

    /*
        아이디가 존재하는 지 확인
    */
    @Override
    @Transactional(readOnly = true)
    public boolean existId(String id) {
        try{
            Account account = readById(id);
            return account == null;
        }catch (IllegalArgumentException a){
            return true;
        }
    }
}
