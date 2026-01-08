package io.why503.theaterservice.model.Entity;

import io.why503.theaterservice.Common.baseConcert;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
public class concert_Item extends baseConcert {

    private LocalDateTime started_at;
    private LocalDateTime ended_at;
    private LocalDateTime reservation_at;
    private LocalDateTime reserved_at;
    private String location;
    private String description;

    @Builder
    public concert_Item(
            LocalDateTime started_at,
            LocalDateTime ended_at,
            LocalDateTime reservation_at,
            LocalDateTime reserved_at,
            String location,
            String description
    ) {
        this.started_at = started_at;
        this.ended_at = ended_at;
        this.reservation_at = reservation_at;
        this.reserved_at = reserved_at;
        this.location = location;
        this.description = description;
    }

    @Builder
    public void update() {
        this.started_at = LocalDateTime.now();
        this.ended_at = LocalDateTime.now();
        this.reservation_at = LocalDateTime.now();
        this.reserved_at = LocalDateTime.now();
    }
}
