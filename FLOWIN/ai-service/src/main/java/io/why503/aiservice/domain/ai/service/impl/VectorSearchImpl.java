package io.why503.aiservice.domain.ai.service.impl;

import io.why503.aiservice.domain.ai.model.embedding.Booking;
import io.why503.aiservice.domain.ai.model.embedding.ShowCategory;
import io.why503.aiservice.domain.ai.model.embedding.genre.*;
import io.why503.aiservice.domain.ai.model.vo.ResultRequest;
import io.why503.aiservice.domain.ai.service.VectorSearch;
import io.why503.aiservice.global.client.ReservationClient;
import io.why503.aiservice.global.client.entity.mapper.BookingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class VectorSearchImpl implements VectorSearch {

    private final VectorStore vectorStore;
    private final ReservationClient reservationClient;
    private final BookingMapper bookingMapper;
    private final EmbeddingModel embeddingModel;


    //사용자가 이 문자열 입력에 의해 임베딩 모델 학습 (텍스트 -> 숫자) / float []
    public float[] embed(ResultRequest request, Long userSq) {

        List<Booking> bookings =
                reservationClient.findMyBookings(userSq)
                        .stream()
                        .filter(bookingResponse -> "PAID".equals(bookingResponse.status()))
                        .map(bookingResponse -> bookingMapper.BookingResponseToBooking(bookingResponse))
                        .toList();

        List<ShowCategory> categories = bookings.stream()
                .filter(b -> "PAID".equalsIgnoreCase(b.status()))
                .map(booking -> ShowCategory.valueOf(booking.category()))
                .toList();

        List<ShowGenre> genres = bookings.stream()
                .filter(b -> "PAID".equalsIgnoreCase(b.status()))
                .map(booking -> {
                    switch (ShowCategory.valueOf(booking.category())) {
                        case MUSICAL:
                            return MusicalType.fromString(booking.genre());
                        case CONCERT:
                            return ConcertType.fromString(booking.genre());
                        case PLAY:
                            return PlayType.fromString(booking.genre());
                        case CLASSIC:
                            return ClassicType.fromString(booking.genre());
                        default:
                            throw new IllegalArgumentException("Unknown category: " + booking.category());
                    }
                })
                .toList();

        String showCategory = categories.stream()
                .map(Category -> Category.name())
                .collect(Collectors.joining(","));

        String genre = genres.stream()
                .map(Genre -> Genre.getName())
                .collect(Collectors.joining(","));

        String text = """
                      사용자의 공연 선호 정보입니다.
                      구매한 카테고리: %s
                      구매한 장르:%s
                      """.formatted(
                showCategory,
                genre
        );
        return embeddingModel.embed(text);
    }

    //카테고리 문서 검색용 함수
    public List<Document> searchCategoryRules(List<ShowCategory> topShowCategory) {
        String query = topShowCategory.stream()
                .map(showCategory -> showCategory.name())
                .collect(Collectors.joining(" "));

        return vectorStore.similaritySearch(
                        SearchRequest.builder()
                                .query(query)
                                .topK(5)
                                .build()
                ).stream()
                .filter(d -> "CATEGORY".equals(d.getMetadata().get("type")))
                .toList();
    }

    //공연 문서 검색용 함수
    public List<Document> searchPerformances(List<ShowCategory> topShowCategory) {
        return vectorStore.similaritySearch(
                        SearchRequest.builder()
                                .query("공연")
                                .topK(50)
                                .build()
                ).stream()
                .filter(d -> "PERFORMANCE".equals(d.getMetadata().get("type")))
                .toList();
    }
}
