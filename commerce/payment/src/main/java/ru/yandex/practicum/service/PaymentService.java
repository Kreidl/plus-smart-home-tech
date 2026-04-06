package ru.yandex.practicum.service;

import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.payment.dto.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {
    PaymentDto createPayment(OrderDto orderDto);

    BigDecimal calculateTotalCost(OrderDto orderDto);

    void successfulPayment(UUID paymentId);

    BigDecimal calculateProductCost(OrderDto orderDto);

    void failedPayment(UUID paymentId);
}
