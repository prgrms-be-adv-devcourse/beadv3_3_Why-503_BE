package io.why503.aiservice.model.vo;

import java.util.List;

public record ResultRequest(
        //사용자가 선호하는 장르
        List<Category> category,
        //사용자가 선호하는 관심
        List<Category> attention,
        //사용자가 선호하는 분위기
        List<MoodCategory> mood
) {}
