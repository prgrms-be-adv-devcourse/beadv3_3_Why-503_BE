package io.why503.accountservice.domain.auth.model.dto;

import io.why503.accountservice.domain.account.model.dto.UserRole;
import lombok.Getter;

/*
나중에 쓸 sq만 있는 클래스,
대부분의 테이블이 회원에게 원하는 것은 회원의 정보가 아니라 회원의 인덱스값이기 때문에
인덱스값만 뽑아서 전달하는 것이 목표, 후에 변경할 가능성 있음
 */
public record TokenBody(
        Long sq
) {
}
