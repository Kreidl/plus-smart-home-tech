package ru.yandex.practicum.feign;

import feign.FeignException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.delivery.dto.DeliveryDto;
import ru.yandex.practicum.feign.fallback.DeliveryFeignFallback;
import ru.yandex.practicum.order.dto.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "delivery", path = "/api/v1/delivery",
        fallback = DeliveryFeignFallback.class)
public interface DeliveryFeign {
    @PutMapping
    DeliveryDto planDelivery(@Valid @RequestBody DeliveryDto deliveryDto) throws FeignException;

    @PostMapping("/successful")
    void successfulDelivery(@NotNull @RequestBody UUID orderId) throws FeignException;

    @PostMapping("/picked")
    void pickedDelivery(@NotNull @RequestBody UUID orderId) throws FeignException;

    @PostMapping("/failed")
    void failedDelivery(@NotNull @RequestBody UUID orderId) throws FeignException;

    @PostMapping("/cost")
    BigDecimal calculateDeliveryCost(@Valid @RequestBody OrderDto orderDto) throws FeignException;
}
