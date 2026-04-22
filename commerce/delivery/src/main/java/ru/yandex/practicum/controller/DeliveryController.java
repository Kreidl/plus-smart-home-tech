package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.delivery.dto.DeliveryDto;
import ru.yandex.practicum.feign.DeliveryFeign;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.service.DeliveryService;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/delivery")
public class DeliveryController implements DeliveryFeign {
    private final DeliveryService deliveryService;

    @Override
    public DeliveryDto planDelivery(DeliveryDto deliveryDto) {
        log.info("New request to plan delivery {}", deliveryDto);
        return deliveryService.planDelivery(deliveryDto);
    }

    @Override
    public void successfulDelivery(UUID orderId) {
        log.info("New request to change delivery state to success, order id = {}", orderId);
        deliveryService.successfulDelivery(orderId);
    }

    @Override
    public void pickedDelivery(UUID orderId) {
        log.info("New request to change delivery state to in progress, order id = {}", orderId);
        deliveryService.pickedDelivery(orderId);
    }

    @Override
    public void failedDelivery(UUID orderId) {
        log.info("New request to change delivery state to failed, order id = {}", orderId);
        deliveryService.failedDelivery(orderId);
    }

    @Override
    public BigDecimal calculateDeliveryCost(OrderDto orderDto) {
        log.info("New request to calculate delivery cost in order, {}", orderDto);
        return deliveryService.calculateDeliveryCost(orderDto);
    }
}
