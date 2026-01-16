package io.why503.paymentservice.client;

import io.why503.paymentservice.client.dto.AccountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "account-service")
public interface AccountClient {
    @GetMapping("/account-sq/{sq}")
    AccountResponse getAccount(@PathVariable("sq") Long userSq);
}