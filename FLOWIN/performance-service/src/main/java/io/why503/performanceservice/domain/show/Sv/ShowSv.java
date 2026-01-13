package io.why503.performanceservice.domain.show.Sv;

import io.why503.performanceservice.domain.show.Model.Dto.ShowReqDto;
import io.why503.performanceservice.domain.show.Model.Dto.ShowResDto;
import io.why503.performanceservice.domain.show.Model.Ett.ShowEtt;

public interface ShowSv {

    ShowResDto createShow(ShowReqDto reqDto);

    ShowResDto getShow(Long showSq);
}