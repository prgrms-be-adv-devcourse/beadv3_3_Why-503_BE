package io.why503.aiservice.domain.ai.model.vo;

import java.util.List;

public record ResultRequest(
        //사용자가 선호하는 장르
        String showCategory,
        //사용자가 선호하는 장르
        String genre

) {}
