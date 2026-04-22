package ru.yandex.practicum.feign.fallback;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.feign.PaymentFeign;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.payment.dto.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
public class PaymentFeignFallback implements PaymentFeign {
    @Override
    public PaymentDto createPayment(OrderDto orderDto) throws FeignException {
        fallback();
        return null;
    }

    @Override
    public BigDecimal calculateTotalCost(OrderDto orderDto) throws FeignException {
        fallback();
        return null;
    }

    @Override
    public void successfulPayment(UUID paymentId) throws FeignException {
        fallback();
    }

    @Override
    public BigDecimal calculateProductCost(OrderDto orderDto) throws FeignException {
        fallback();
        return null;
    }

    @Override
    public void failedPayment(UUID paymentId) throws FeignException {
        fallback();
    }

    private void fallback() {
        log.error("Fallback: payment service is not responding");
        throw new RuntimeException("Payment service is not responding, please try again later");
    }
}
