package ru.yandex.practicum.feign.fallback;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.delivery.dto.DeliveryDto;
import ru.yandex.practicum.feign.DeliveryFeign;
import ru.yandex.practicum.order.dto.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
public class DeliveryFeignFallback implements DeliveryFeign {
    @Override
    public DeliveryDto planDelivery(DeliveryDto deliveryDto) throws FeignException {
        fallback();
        return null;
    }

    @Override
    public void successfulDelivery(UUID orderId) throws FeignException {
        fallback();
    }

    @Override
    public void pickedDelivery(UUID orderId) throws FeignException {
        fallback();
    }

    @Override
    public void failedDelivery(UUID orderId) throws FeignException {
        fallback();
    }

    @Override
    public BigDecimal calculateDeliveryCost(OrderDto orderDto) throws FeignException {
        fallback();
        return null;
    }

    private void fallback() {
        log.error("Fallback: delivery service is not responding");
        throw new RuntimeException("Delivery service is not responding, please try again later");
    }
}
