package io.why503.gatewayservice.config;


import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.security.PublicKey;

import static org.mockito.Mockito.mock;

@TestConfiguration
@Profile("test")
public class TestKeyConfig {

    @Bean
    PublicKey publicKey() {
        return mock(PublicKey.class);
    }
}
