package io.why503.theaterservice.model.Ett;

import io.why503.theaterservice.Common.baseConcert;
import io.why503.theaterservice.model.Dto.hallReq;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Getter
@NoArgsConstructor
public class concert_hallEtt extends baseConcert {

//    private int sq;
    private String name;
    private String post;
    private String basic_addr;
    private String detail_addr;
    private int stat;
    private int seat_scale;
    private String structure;
    private int latitude;
    private int longitude;

    @Builder
    public concert_hallEtt(
         String name,
         String post,
         String basic_addr,
         String detail_addr,
         int stat,
         int seat_scale,
         String structure,
         int latitude,
         int longitude
    ) {
        this.name = name;
        this.post = post;
        this.basic_addr = basic_addr;
        this.detail_addr = detail_addr;
        this.stat = stat;
        this.seat_scale = seat_scale;
        this.structure = structure;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void update(
            String name,
            String post,
            String basic_addr,
            String detail_addr,
            int stat,
            int seat_scale,
            String structure,
            int latitude,
            int longitude
    ) {
        this.name = name;
        this.post = post;
        this.basic_addr = basic_addr;
        this.detail_addr = detail_addr;
        this.stat = stat;
        this.seat_scale = seat_scale;
        this.structure = structure;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void update(hallReq hallReq) {
    }
}
