package io.why503.aiservice.domain.ai.service.impl;

import io.why503.aiservice.domain.ai.model.dto.response.RecommendResponse;
import io.why503.aiservice.domain.ai.util.AiExceptionFactory;
import io.why503.aiservice.domain.ai.util.mapper.*;
import io.why503.aiservice.domain.ai.service.AiService;
import io.why503.aiservice.global.client.reservation.ReservationClient;
import io.why503.aiservice.global.client.reservation.model.dto.response.BookingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * 추천과 검색을 하기 위해서는 텍스트를 숫자 벡터로 변환하여 유사도를 계산한다
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final VectorStoreMapper vectorStoreMapper;
    private final RecommendationMapper recommendationMapper;
    private final ReservationClient reservationClient;
    private final VectorStoreSearchImpl vectorSearch;


    //추천 받는 명령어 (프롬프트에 명령한 규칙 수행)
    public List<RecommendResponse> getRecommendations(Long userSq) {

        List<BookingResponse> bookings = reservationClient.findMyBookings(userSq);

        if(bookings.isEmpty()){
            throw AiExceptionFactory.AiNotFound("구매내역 없음");
        }

        //유저의 최빈 카테고리 및 장르 탐색
        Map<String, Integer> categoryMap = new HashMap<>();
        Map<String, Integer> genreMap = new HashMap<>();

        for(BookingResponse response : bookings){
            String category = response.category();
            String genre = response.genre();

            categoryMap.put(category, categoryMap.getOrDefault(category, 0) + 1);
            genreMap.put(genre, genreMap.getOrDefault(genre, 0) + 1);
        }
        String topCategory = findTopKeyByMap(categoryMap);
        String topGenre = findTopKeyByMap(genreMap);

        //검색을 위한 string 생성
        String query = vectorStoreMapper.SearchToQuery(topCategory, topGenre);

        //공연 문서 검색
        List<Document> performanceDocs = vectorSearch.searchPerformances(query);

        if(performanceDocs.isEmpty()){
            throw AiExceptionFactory.AiNotFound("검색된 추천내역 없음");
        }

        return recommendationMapper.DocsToRecommendList(performanceDocs);

    }
    //map에서 최빈도 값 찾는 함수
    private String findTopKeyByMap(Map<String, Integer> map){
        String topKey = null;
        Integer maxCount = 0;
        for(Map.Entry<String, Integer> atom : map.entrySet()){
            if(atom.getValue() > maxCount){
                maxCount = atom.getValue();
                topKey = atom.getKey();
            }
        }
        if(topKey == null){
            throw AiExceptionFactory.AiNotFound("유저의 구매내역이 없음");
        }
        return topKey;
    }
}
