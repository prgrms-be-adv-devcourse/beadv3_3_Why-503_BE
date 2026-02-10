package io.why503.performanceservice.global.client.paymentservice;

import io.why503.performanceservice.global.client.paymentservice.dto.TicketCreateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service")
public interface PaymentServiceClient {

    @PostMapping("/tickets/init")
    void createTicketSlots(@RequestBody TicketCreateRequest request);
}
