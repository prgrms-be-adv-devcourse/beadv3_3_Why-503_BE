package io.why503.accountservice.domain.accounts.service.impl;

import io.why503.accountbase.model.enums.UserRole;
import io.why503.accountservice.domain.accounts.model.dto.response.UserCompanyResponse;
import io.why503.accountservice.domain.accounts.model.dto.response.UserPointResponse;
import io.why503.accountservice.domain.accounts.model.dto.response.UserRoleResponse;
import io.why503.accountservice.domain.accounts.util.AccountExceptionFactory;
import io.why503.accountservice.domain.accounts.util.AccountMapper;
import io.why503.accountservice.domain.accounts.model.dto.requests.CreateAccountRequest;
import io.why503.accountservice.domain.accounts.model.entity.Account;
import io.why503.accountservice.domain.accounts.repository.AccountJpaRepository;
import io.why503.accountservice.domain.accounts.service.AccountService;
import io.why503.accountservice.domain.companies.model.entity.Company;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
account service 인터페이스 실체화 클래스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountServiceImpl implements AccountService {
    private final AccountJpaRepository accountJpaRepository;
    private final AccountMapper accountMapper;
    //생성, accountMapper로 password암호화
    //내부 함수 sq기반 조회
    @Override
    public Account findBySq(Long sq) {
        return accountJpaRepository.findBySq(sq).orElseThrow(
                () -> AccountExceptionFactory.accountNotFound("sq = "+ sq +" Account is not found")
        );

    }
    //내부 함수 id기반 조회
    @Override
    public Account findById(String id) {
        return accountJpaRepository.findById(id).orElseThrow(
                () -> AccountExceptionFactory.accountNotFound("id = " + id + " Account is not found")
        );
    }
    @Override
    @Transactional
    public UserRoleResponse create(CreateAccountRequest request){
        Account account = accountMapper.upsertRequestToEntity(request);
        if(accountJpaRepository.existsById(account.getId())){
            throw AccountExceptionFactory.accountConflict("id exist");
        }
        accountJpaRepository.save(account);
        return accountMapper.entityToRoleResponse(account);
    }
    //모든 회원 조회
    @Override
    public List<UserRoleResponse> readAll() {
        return accountJpaRepository.findAll().stream()
                .map((account) -> accountMapper.entityToRoleResponse(account))
                .toList();
    }
    //sq기반 조회
    @Override
    public UserRoleResponse readBySq(Long sq) {
        Account account = findBySq(sq);
        return accountMapper.entityToRoleResponse(account);
    }
    //id기반 조회
    @Override
    public UserRoleResponse readById(String id) {
        Account account = findById(id);
        return accountMapper.entityToRoleResponse(account);
    }
    //sq로 UserRole 조회
    @Override
    public UserRole readUserRoleBySq(Long sq) {
        return findBySq(sq).getRole();
    }

    //sq기반 삭제
    @Override
    @Transactional
    public UserRoleResponse deleteBySq(Long sq) {
        Account account = findBySq(sq);
        try {
            accountJpaRepository.delete(account);
        } catch (Exception e) {
            throw AccountExceptionFactory.accountConflict("delete fail");
        }
        return accountMapper.entityToRoleResponse(account);
    }
    //id기반 삭제
    @Override
    @Transactional
    public UserRoleResponse deleteById(String id) {
        Account account = findById(id);
        try {
            accountJpaRepository.delete(account);
        } catch (Exception e) {
            throw AccountExceptionFactory.accountConflict("delete fail");
        }
        return accountMapper.entityToRoleResponse(account);
    }
    //아이디가 존재하는 지 확인
    @Override
    public boolean existId(String id) {
        return accountJpaRepository.existsById(id);
    }
    //포인트, 이름 반환
    @Override
    public UserPointResponse readPointBySq(Long sq) {
        Account account = findBySq(sq);
        return accountMapper.entityToPointResponse(account);
    }

    //유저에 연결된 회사 시퀸스 넘버 반환
    @Override
    public UserCompanyResponse readCompanyBySq(Long sq) {
        Account account = findBySq(sq);
        //일단 null이라도 리턴
        return accountMapper.entityToCompanyResponse(account);
    }
    //포인트 증가
    @Override
    @Transactional
    public UserRoleResponse increasePoint(Long sq, Long point) {
        Account account = findBySq(sq);
        account.increasePoint(point);
        return accountMapper.entityToRoleResponse(account);
    }
    //포인트 감소
    @Override
    @Transactional
    public UserRoleResponse decreasePoint(Long sq, Long point) {
        Account account = findBySq(sq);
        account.decreasePoint(point);
        return accountMapper.entityToRoleResponse(account);
    }

    //sq로 UserRole 수정
    @Override
    @Transactional
    public void grantAccount(Long sq, UserRole role) {
        Account account = findBySq(sq);
        account.setRole(role);
    }
    //회사 주입
    @Override
    @Transactional
    public UserRoleResponse joinCompany(Long userSq, Company company, UserRole role) {
        Account account = findBySq(userSq);
        account.joinCompany(company, role);
        return accountMapper.entityToRoleResponse(account);
    }
    //회사 탈퇴
    @Override
    @Transactional
    public UserRoleResponse leaveCompany(Long userSq) {
        Account account = findBySq(userSq);
        account.leaveCompany();
        account.setRole(UserRole.USER);
        return accountMapper.entityToRoleResponse(account);
    }
    //회사에서 company = {companySq} 가입된 유저를 검색할 때 사용
    @Override
    public List<UserRoleResponse> readCompanyMember(Long companySq) {
        return accountJpaRepository.findByCompany_Sq(companySq)
                .stream()
                .map(i -> accountMapper.entityToRoleResponse(i))
                .toList();
    }
}
