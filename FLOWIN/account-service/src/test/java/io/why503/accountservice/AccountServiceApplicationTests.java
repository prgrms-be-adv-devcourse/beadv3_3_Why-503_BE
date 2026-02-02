package io.why503.accountservice;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;


@Slf4j
@ActiveProfiles("test")
@SpringBootTest
class AccountServiceApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void http_printer(){
        log.info(Integer.toString(HttpStatus.OK.value()));
    }

}
