package io.why503.aiservice.model.vo;

import java.util.List;

public record ResultRequest(
        //리스트 형태 장르
        List<Category> category,
        //리스트 형태 관심 장르
        List<Category> attention,
        List<MoodCategory> mood
) {}
