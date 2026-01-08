package io.why503.reservationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
// ★★★ 아래 2줄을 복사해서 붙여넣으세요! ★★★
@EnableJpaRepositories(basePackages = "io.why503.reservationservice")
@EntityScan(basePackages = "io.why503.reservationservice")
public class ReservationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReservationServiceApplication.class, args);
    }
}