package ru.yandex.practicum.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.exception.model.NoPaymentFoundException;
import ru.yandex.practicum.exception.model.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.feign.OrderFeign;
import ru.yandex.practicum.feign.ShoppingStoreFeign;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.payment.dto.PaymentDto;
import ru.yandex.practicum.payment.enums.PaymentState;
import ru.yandex.practicum.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderFeign orderFeign;
    private final ShoppingStoreFeign shoppingStoreFeign;
    private static final BigDecimal TAX_PERCENTAGE = BigDecimal.valueOf(0.1);

    @Override
    public PaymentDto createPayment(OrderDto orderDto) {
        if(orderDto == null || orderDto.deliveryPrice() == null || orderDto.productPrice() == null
                || orderDto.totalPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Not enough info in order to calculate");
        }
        BigDecimal feeTotal = orderDto.productPrice().multiply(TAX_PERCENTAGE);
        Payment payment = PaymentMapper.mapToEntity(orderDto, feeTotal);
        payment.setPaymentState(PaymentState.PENDING);
        return PaymentMapper.mapToDto(paymentRepository.save(payment));
    }

    @Override
    public BigDecimal calculateTotalCost(OrderDto orderDto) {
        if(orderDto.deliveryPrice() == null || orderDto.productPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Not enough info in order to calculate");
        }
        BigDecimal feeTotal = orderDto.productPrice().multiply(TAX_PERCENTAGE);
        BigDecimal totalCost = orderDto.productPrice().add(orderDto.deliveryPrice()).add(feeTotal);
        return totalCost;
    }

    @Override
    public void successfulPayment(UUID paymentId) {
        Payment payment = getPaymentById(paymentId);
        payment.setPaymentState(PaymentState.SUCCESS);
        paymentRepository.save(payment);
        try {
            orderFeign.successOrderPayment(payment.getOrderId());
        } catch (FeignException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public BigDecimal calculateProductCost(OrderDto orderDto) {
        Map<UUID, Long> products = orderDto.products();
        if(products.isEmpty()) {
            throw new NotEnoughInfoInOrderToCalculateException("Not enough info in order to calculate");
        }
        BigDecimal productCost = BigDecimal.valueOf(0.0);
        for(UUID productId : products.keySet()) {
            BigDecimal price;
            try {
                price = shoppingStoreFeign.getProductById(productId).price();
            } catch (FeignException e) {
                throw new RuntimeException(e.getMessage());
            }
            Long quantity = products.get(productId);
            productCost = productCost.add(price.multiply(BigDecimal.valueOf(quantity)));
        }
        return productCost;
    }

    @Override
    public void failedPayment(UUID paymentId) {
        Payment payment = getPaymentById(paymentId);
        payment.setPaymentState(PaymentState.FAILED);
        paymentRepository.save(payment);
        try {
            orderFeign.failedOrderPayment(payment.getOrderId());
        } catch (FeignException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Payment getPaymentById(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NoPaymentFoundException("Payment with id " + paymentId + " not found"));
    }
}
