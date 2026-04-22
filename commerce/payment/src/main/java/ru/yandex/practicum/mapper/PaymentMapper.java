package ru.yandex.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.payment.dto.PaymentDto;

import java.math.BigDecimal;

@UtilityClass
public class PaymentMapper {
    public static PaymentDto mapToDto(Payment payment) {
        return new PaymentDto(payment.getPaymentId(), payment.getTotalPayment(), payment.getDeliveryTotal(),
                payment.getFeeTotal());
    }

    public static Payment mapToEntity(OrderDto orderDto, BigDecimal feeTotal) {
        return new Payment(orderDto.paymentId(), orderDto.orderId(), orderDto.totalPrice(),
                orderDto.deliveryPrice(), feeTotal, orderDto.productPrice(), null);
    }
}
