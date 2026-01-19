package io.why503.performanceservice.domain.showseat.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ShowSeatGradeChangeRequest {

    private String grade; // VIP, R, S, A ...
}
