package io.why503.aiservice.controller;

import io.why503.aiservice.model.embedding.Category;
import org.springframework.ai.document.Document;

import java.util.HashMap;

public class CategoryDocument {
    public static Document create(Category c) {
        String content = switch (c) {
            case MUSICAL -> """
                    장르: MUSICAL
                    설명: 음악과 스토리가 결합된 공연 형식
                    공연 구성: 노래 중심, 대사와 연기 결합
                    관람 경험: 감정 몰입형, 서사 중심
                    특징: 넘버, 무대 연출, 캐릭터 감정선
                    연관 분위기: ROMANCE, FANTASY
                    대표 공연 예: 오페라의 유령, 위키드, 레미제라블
                    추천 대상: 이야기와 음악을 함께 즐기고 싶은 관객
                    """;
            case CONCERT -> """
                    장르: CONCERT
                    설명: 라이브 음악을 중심으로 한 공연 형식
                    공연 구성: 연주와 노래 중심, 대사 거의 없음
                    관람 경험: 현장 몰입형, 참여형
                    특징: 아티스트 중심, 라이브 사운드, 현장감
                    연관 분위기: ACTION, COMEDY
                    대표 공연 예: 내한 콘서트, 오케스트라 라이브, 밴드 공연
                    추천 대상: 현장 에너지와 음악에 몰입하고 싶은 관객
                    """;
            case PLAY -> """
                    장르: PLAY
                    설명: 대사와 연기를 중심으로 이야기를 전달하는 공연 형식
                    공연 구성: 대사 중심, 연기 비중 높음
                    관람 경험: 사고 몰입형, 해석 중심
                    특징: 메시지, 배우 연기력, 연출 의도
                    연관 분위기: DRAMA, ROMANCE
                    대표 공연 예: 햄릿, 세일즈맨의 죽음, 현대극 작품
                    추천 대상: 스토리 해석과 메시지를 즐기는 관객
                    """;
            case CLASSIC -> """
                    장르: CLASSIC
                    설명: 클래식 음악을 기반으로 한 공연 형식
                    공연 구성: 연주 중심, 노래 또는 대사 거의 없음
                    관람 경험: 정제된 감상형, 집중 청취
                    특징: 오케스트라, 음향, 공간 울림
                    연관 분위기: FANTASY, ROMANCE
                    대표 공연 예: 교향곡 연주회, 실내악 공연, 협주곡 무대
                    추천 대상: 차분한 분위기에서 음악 감상을 원하는 관객
                    """;
        };

        HashMap<String, Object> meta = new HashMap<>();
        meta.put("type", "CATEGORY");
        meta.put("category", c.name());
        meta.put("mood", c.getMood());

        return Document.builder()
                .id("category_" + c.name())
                .text(content)
                .metadata(meta)
                .build();

    }
}
