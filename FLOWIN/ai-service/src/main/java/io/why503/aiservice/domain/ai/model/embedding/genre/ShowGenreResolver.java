package io.why503.aiservice.domain.ai.model.embedding.genre;

import io.why503.aiservice.domain.ai.model.embedding.genre.impl.ShowGenre;
import io.why503.aiservice.global.exception.AiException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *  카테고리와 장르 같이 매핑 처리용 클래스
 *  서비스 로직에 캐시 방식으로 적용 -> toDomain
 *  카테고리 안에 장르 찾고 장르 안에 카테고리 찾는 구조를 개선
 *
 */
@Component
public class ShowGenreResolver {

    private final Map<String, ShowGenre> genreMap = new HashMap<>();

    public ShowGenreResolver(List<ShowGenre> genres) {
        genres.forEach(g ->
                genreMap.put(g.getName().toUpperCase(), g)
        );
    }

    public ShowGenre fromString(String value) {
        if (value == null || value.isBlank()) {
            throw AiException.invalidGenre();
        }

        ShowGenre genre = genreMap.get(value.trim().toUpperCase());

        if (genre == null) {
            throw AiException.invalidGenre();
        }

        return genre;
    }

}
