package io.why503.performanceservice.global.validator;

import io.why503.accountbase.model.enums.UserRole;
import io.why503.commonbase.exception.CustomException;
import io.why503.performanceservice.global.client.accountservice.AccountServiceClient;
import io.why503.performanceservice.global.client.accountservice.dto.UserRoleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final AccountServiceClient accountServiceClient;

    // 기업 또는 관리자 권한 검증

    public void validateEnterprise(Long userSq, CustomException forbiddenException) {
        validateRoles(
                userSq,
                List.of(UserRole.COMPANY, UserRole.ADMIN),
                forbiddenException
        );
    }

    // 관리자 권한 검증
    public void validateAdmin(Long userSq, CustomException forbiddenException) {
        validateRoles(
                userSq,
                List.of(UserRole.ADMIN),
                forbiddenException
        );
    }

    // 공통
    private void validateRoles(
            Long userSq,
            List<UserRole> requiredRoles,
            CustomException forbiddenException
    ) {

        // 유저 권한 조회
        UserRoleResponse roleInfo = accountServiceClient.getUserRole(userSq);

        // 유저 정보 조회 실패
        if (roleInfo == null) {
            throw forbiddenException;
        }

        UserRole currentRole = roleInfo.userRole();

        // 권한 불충분
        if (!requiredRoles.contains(currentRole)) {
            throw forbiddenException;
        }
    }
}
