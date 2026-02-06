package io.why503.aiservice.model.vo;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

//공연의 종류마다 이름을 찾거나 category 책임 분리하여 복잡한 코드 -> 단순화 코드 (공연의 종류 찾을 때)
public interface ShowCategory {

    //아직 없음
    Category getCategory();
    //공연 이름
    String typeName();
    //분위기
    Set<MoodCategory> moods();

    //선정된 분위기 설정
    default MoodCategory pickMood(Random random) {
        if (moods() == null || moods().isEmpty()) {
            return null;
        }
        List<MoodCategory> list = new ArrayList<>(moods());
        return list.get(random.nextInt(list.size()));
    }
}
