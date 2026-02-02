package io.why503.performanceservice.global.validator;

import io.why503.performanceservice.global.client.accountservice.AccountServiceClient;
import io.why503.performanceservice.global.client.accountservice.dto.UserRoleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final AccountServiceClient accountServiceClient;

    private static final int ROLE_ADMIN = 0;
    private static final int ROLE_COMPANY = 2;

    // 기업, 관리자 권한 검증
    public void validateEnterprise(Long userSq) {

        validateRoles(userSq, "기업 또는 관리자 권한이 필요합니다.", List.of(ROLE_COMPANY, ROLE_ADMIN));
    }

    // 관리자 권한 검증
    public void validateAdmin(Long userSq) {

        validateRoles(userSq, "관리자 권한이 필요합니다.", List.of(ROLE_ADMIN));
    }

    // Role 비교
    private void validateRoles(Long userSq, String errorMessage, List<Integer> requiredRoles) {

        // 유저 정보 조회
        UserRoleResponse roleInfo = accountServiceClient.getUserRole(userSq);

        // 조회 실패 시 예외 처리
        if (roleInfo == null) {
            throw new IllegalArgumentException("유저 정보를 찾을 수 없습니다.");
        }

        int currentRole = roleInfo.userRole(); // 현재 유저의 권한

        // 현재 유저의 권한에 대한 숫자가 리스트 안에 없으면 에러
        if (!requiredRoles.contains(currentRole)) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}