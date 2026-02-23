package io.why503.performanceservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PerformanceServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PerformanceServiceApplication.class, args);
	}

}