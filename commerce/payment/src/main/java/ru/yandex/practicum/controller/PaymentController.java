package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.feign.PaymentFeign;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.payment.dto.PaymentDto;
import ru.yandex.practicum.service.PaymentService;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController implements PaymentFeign {
    private final PaymentService paymentService;

    @Override
    public PaymentDto createPayment(OrderDto orderDto) {
        log.info("New request to create payment for order = {}", orderDto);
        return paymentService.createPayment(orderDto);
    }

    @Override
    public BigDecimal calculateTotalCost(OrderDto orderDto) {
        log.info("New request to calculate total cost for order = {}", orderDto);
        return paymentService.calculateTotalCost(orderDto);
    }

    @Override
    public void successfulPayment(UUID paymentId) {
        log.info("New request to change payment state to success for payment with id = {}", paymentId);
        paymentService.successfulPayment(paymentId);
    }

    @Override
    public BigDecimal calculateProductCost(OrderDto orderDto) {
        log.info("New request to calculate product cost for order = {}", orderDto);
        return paymentService.calculateProductCost(orderDto);
    }

    @Override
    public void failedPayment(UUID paymentId) {
        log.info("New request to change payment state to failed for payment with id = {}", paymentId);
        paymentService.failedPayment(paymentId);
    }
}
