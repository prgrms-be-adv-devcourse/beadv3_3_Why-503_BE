package io.why503.performanceservice.domain.show.Sv;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.why503.performanceservice.domain.show.Model.Dto.ShowReqDto;
import io.why503.performanceservice.domain.show.Model.Dto.ShowResDto;
import io.why503.performanceservice.domain.show.Model.Ett.ShowEtt;
import io.why503.performanceservice.domain.show.Model.Enum.ShowCategory;
import io.why503.performanceservice.domain.show.Model.Enum.ShowStatus;
import io.why503.performanceservice.domain.show.Repo.ShowRepo;
import io.why503.performanceservice.domain.show.Sv.ShowSv;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShowSvImpl implements ShowSv {

    private final ShowRepo showRepo;

    @Override
    @Transactional
    public ShowResDto createShow(ShowReqDto reqDto) {

        ShowCategory category = ShowCategory.fromCode(reqDto.getCategory());

        ShowEtt show = ShowEtt.builder()
                .showName(reqDto.getShowName())
                .startDate(reqDto.getStartDate())
                .endDate(reqDto.getEndDate())
                .openDt(reqDto.getOpenDt())
                .showTime(reqDto.getShowTime())
                .viewingAge(reqDto.getViewingAge())
                .concertHallSq(reqDto.getConcertHallSq())
                .companySq(reqDto.getCompanySq())
                .build();

        // enum → code 세팅
        show.setCategory(category);
        show.setShowStatus(ShowStatus.SCHEDULED);

        ShowEtt saved = showRepo.save(show);

        return ShowResDto.builder()
                .showSq(saved.getShowSq())
                .showName(saved.getShowName())
                .startDate(saved.getStartDate())
                .endDate(saved.getEndDate())
                .openDt(saved.getOpenDt())
                .showTime(saved.getShowTime())
                .viewingAge(saved.getViewingAge())
                .category(saved.getCategoryEnum())
                .showStat(saved.getShowStatus())
                .concertHallSq(saved.getConcertHallSq())
                .companySq(saved.getCompanySq())
                .build();
    }
    @Override
    public ShowResDto getShow(Long showSq) {

        ShowEtt show = showRepo.findById(showSq)
                .orElseThrow(() -> new IllegalArgumentException("show not found"));

        return ShowResDto.builder()
            .showSq(show.getShowSq())
            .showName(show.getShowName())
            .startDate(show.getStartDate())
            .endDate(show.getEndDate())
            .openDt(show.getOpenDt())
            .showTime(show.getShowTime())
            .viewingAge(show.getViewingAge())
            .category(show.getCategoryEnum())
            .showStat(show.getShowStatus())
            .concertHallSq(show.getConcertHallSq())
            .companySq(show.getCompanySq())
            .build();
        }

}
