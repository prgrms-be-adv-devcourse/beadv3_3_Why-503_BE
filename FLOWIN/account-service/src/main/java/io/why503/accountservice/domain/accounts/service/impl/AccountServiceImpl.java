package io.why503.accountservice.domain.accounts.service.impl;


import io.why503.accountservice.domain.accounts.model.dto.response.UserCompanyResponse;
import io.why503.accountservice.domain.accounts.model.dto.response.UserPointResponse;
import io.why503.accountservice.domain.accounts.model.dto.response.UserSummaryResponse;
import io.why503.accountservice.domain.accounts.model.enums.UserRole;
import io.why503.accountservice.util.AccountMapper;
import io.why503.accountservice.domain.accounts.model.dto.requests.UpsertAccountRequest;
import io.why503.accountservice.domain.accounts.model.entity.Account;
import io.why503.accountservice.domain.accounts.repository.AccountJpaRepository;
import io.why503.accountservice.domain.accounts.service.AccountService;
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
public class AccountServiceImpl implements AccountService {
    private final AccountJpaRepository accountJpaRepository;
    private final AccountMapper accountMapper;
    //생성, accountMapper로 password암호화
    //내부 함수 sq기반 조회
    @Transactional(readOnly = true)
    public Account findBySq(Long sq) {
        return accountJpaRepository.findBySq(sq).orElseThrow(
                () -> new IllegalArgumentException("sq = " + sq + " Account is not found")
        );

    }
    //내부 함수 id기반 조회
    @Transactional(readOnly = true)
    public Account findById(String id) {
        return accountJpaRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("id = " + id + " Account is not found")
        );
    }
    @Override
    @Transactional
    public UserSummaryResponse create(UpsertAccountRequest request){

        Account account = new Account(
                accountMapper.upsertDtoToUpsertVo(request)
        );
        accountJpaRepository.save(account);
        return accountMapper.entityToSummaryResponse(account);
    }
    //모든 회원 조회
    @Override
    @Transactional(readOnly = true)
    public List<UserSummaryResponse> readAll() {
        return accountJpaRepository.findAll().stream()
                .map((account) -> accountMapper.entityToSummaryResponse(account))
                .toList();
    }
    //sq기반 조회
    @Override
    @Transactional(readOnly = true)
    public UserSummaryResponse readBySq(Long sq) {
        Account account = findBySq(sq);
        return accountMapper.entityToSummaryResponse(account);
    }
    //id기반 조회
    @Override
    @Transactional(readOnly = true)
    public UserSummaryResponse readById(String id) {
        Account account = findById(id);
        return accountMapper.entityToSummaryResponse(account);
    }
    //sq로 UserRole 조회
    @Override
    @Transactional(readOnly = true)
    public UserRole readUserRoleBySq(Long sq) {
        return findBySq(sq).getRole();
    }

    //sq기반 수정
    @Override
    @Transactional
    public UserSummaryResponse updateBySq(Long sq, UpsertAccountRequest request) {
        Account account = findBySq(sq);
        account.update(accountMapper.upsertDtoToUpsertVo(request));
        return accountMapper.entityToSummaryResponse(account);
    }
    //id기반 수정
    @Override
    @Transactional
    public UserSummaryResponse updateById(String id, UpsertAccountRequest request) {
        Account account = findById(id);
        account.update(accountMapper.upsertDtoToUpsertVo(request));
        return accountMapper.entityToSummaryResponse(account);
    }
    //sq기반 삭제
    @Override
    @Transactional
    public UserSummaryResponse deleteBySq(Long sq) {
        Account account = findBySq(sq);
        accountJpaRepository.delete(account);
        return accountMapper.entityToSummaryResponse(account);
    }
    //id기반 삭제
    @Override
    @Transactional
    public UserSummaryResponse deleteById(String id) {
        Account account = findById(id);
        accountJpaRepository.delete(account);
        return accountMapper.entityToSummaryResponse(account);
    }
    //아이디가 존재하는 지 확인
    @Override
    @Transactional(readOnly = true)
    public boolean existId(String id) {
        try{
            Account account = findById(id);
            return account == null;
        }catch (IllegalArgumentException a){
            return true;
        }
    }
    //포인트 이름 반환
    @Override
    @Transactional(readOnly = true)
    public UserPointResponse readPointBySq(Long sq) {
        Account account = findBySq(sq);
        return accountMapper.entityToPointResponse(account);
    }

    //유저에 연결된 회사 시퀸스 넘버 반환
    @Override
    @Transactional(readOnly = true)
    public UserCompanyResponse readCompanyBySq(Long sq) {
        Account account = findBySq(sq);
        if(account.getRole() == UserRole.COMPANY) {
            return accountMapper.entityToCompanyResponse(account);
        }
        else{
            return null;
        }
    }

    //sq로 UserRole 수정
    @Override
    @Transactional
    public UserSummaryResponse updateUserRoleBySq(Long sq, UserRole role) {
        Account account = findBySq(sq);
        account.setRole(role);
        return accountMapper.entityToSummaryResponse(account);
    }

}
