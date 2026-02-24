package io.why503.aiservice.domain.ai.model.vo;

import io.why503.aiservice.domain.ai.model.embedding.ShowCategory;
import io.why503.aiservice.domain.ai.model.embedding.genre.impl.ShowGenre;

import java.util.List;

public record ResultRequest(
        //사용자가 선호하는 장르
        List<ShowCategory> showCategory,
        //사용자가 선호하는 장르
        List<ShowGenre> genre

) {}
