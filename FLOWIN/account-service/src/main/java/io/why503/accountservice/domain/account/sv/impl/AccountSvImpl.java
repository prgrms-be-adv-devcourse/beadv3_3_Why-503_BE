package io.why503.accountservice.domain.account.sv.impl;


import io.why503.accountservice.util.AccountMapper;
import io.why503.accountservice.domain.account.model.dto.req.UpsertAccountReq;
import io.why503.accountservice.domain.account.model.ett.Account;
import io.why503.accountservice.domain.account.repo.AccountRepo;
import io.why503.accountservice.domain.account.sv.AccountSv;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
account service 인터페이스 실체화 클래스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountSvImpl implements AccountSv {
    private final AccountRepo accountRepo;
    private final AccountMapper accountMapper;
    //생성, accountMapper로 password암호화
    @Override
    @Transactional
    public Account create(UpsertAccountReq request){

        Account account = new Account(accountMapper.upsertDtoToUpsertCmd(request));
        return accountRepo.save(account);
    }
    //모든 회원 조회
    @Override
    @Transactional(readOnly = true)
    public List<Account> readAll() {
        return accountRepo.findAll();
    }
    //sq기반 조회
    @Override
    @Transactional(readOnly = true)
    public Account readBySq(Long sq) {
        return accountRepo.findBySq(sq).orElseThrow(
                () -> new IllegalArgumentException("sq = " + sq + " Account is not found")
        );
    }
    //id기반 조회
    @Override
    @Transactional(readOnly = true)
    public Account readById(String id) {
        return accountRepo.findById(id).orElseThrow(
                () -> new IllegalArgumentException("id = " + id + " Account is not found")
        );
    }
    //sq기반 수정
    @Override
    @Transactional
    public Account updateBySq(Long sq, UpsertAccountReq request) {
        Account account = readBySq(sq);
        account.update(accountMapper.upsertDtoToUpsertCmd(request));
        return account;
    }
    //id기반 수정
    @Override
    @Transactional
    public Account updateById(String id, UpsertAccountReq request) {
        Account account = readById(id);
        account.update(accountMapper.upsertDtoToUpsertCmd(request));
        return account;
    }
    //sq기반 삭제
    @Override
    @Transactional
    public Account deleteBySq(Long sq) {
        Account account = readBySq(sq);
        accountRepo.delete(account);
        return account;
    }
    //id기반 삭제
    @Override
    @Transactional
    public Account deleteById(String id) {
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
