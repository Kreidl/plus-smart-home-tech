package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.delivery.dto.DeliveryDto;
import ru.yandex.practicum.feign.DeliveryFeign;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.service.DeliveryService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/delivery")
public class DeliveryController implements DeliveryFeign {
    private final DeliveryService deliveryService;

    @Override
    public DeliveryDto planDelivery(DeliveryDto deliveryDto) {
        return deliveryService.planDelivery(deliveryDto);
    }

    @Override
    public void successfulDelivery(UUID orderId) {
        deliveryService.successfulDelivery(orderId);
    }

    @Override
    public void pickedDelivery(UUID orderId) {
        deliveryService.pickedDelivery(orderId);
    }

    @Override
    public void failedDelivery(UUID orderId) {
        deliveryService.failedDelivery(orderId);
    }

    @Override
    public BigDecimal calculateDeliveryCost(OrderDto orderDto) {
        return deliveryService.calculateDeliveryCost(orderDto);
    }
}
