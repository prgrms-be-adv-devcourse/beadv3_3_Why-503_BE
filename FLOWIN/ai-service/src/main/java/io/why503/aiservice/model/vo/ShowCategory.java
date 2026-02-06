package io.why503.aiservice.model.vo;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

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
