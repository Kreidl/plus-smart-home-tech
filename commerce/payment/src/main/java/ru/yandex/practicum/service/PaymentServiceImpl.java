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
import ru.yandex.practicum.store.dto.ProductDto;

import java.math.BigDecimal;
import java.util.List;
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
        log.info("Starting to create payment");
        if(orderDto == null || orderDto.deliveryPrice() == null || orderDto.productPrice() == null
                || orderDto.totalPrice() == null) {
            log.warn("Not enough info in order {} to calculate", orderDto);
            throw new NotEnoughInfoInOrderToCalculateException("Not enough info in order to calculate");
        }
        BigDecimal feeTotal = orderDto.productPrice().multiply(TAX_PERCENTAGE);
        Payment payment = PaymentMapper.mapToEntity(orderDto, feeTotal);
        payment.setPaymentState(PaymentState.PENDING);
        payment = paymentRepository.save(payment);
        log.debug("Payment created, {}", payment);
        return PaymentMapper.mapToDto(payment);
    }

    @Override
    public BigDecimal calculateTotalCost(OrderDto orderDto) {
        log.info("Starting to calculate total cost");
        if(orderDto.deliveryPrice() == null || orderDto.productPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Not enough info in order to calculate");
        }
        BigDecimal feeTotal = orderDto.productPrice().multiply(TAX_PERCENTAGE);
        BigDecimal totalCost = orderDto.productPrice().add(orderDto.deliveryPrice()).add(feeTotal);
        log.debug("Total cost for order with id = {} calculated, {}", orderDto.orderId(), totalCost);
        return totalCost;
    }

    @Override
    public void successfulPayment(UUID paymentId) {
        log.info("Starting to change payment state to success");
        Payment payment = getPaymentById(paymentId);
        payment.setPaymentState(PaymentState.SUCCESS);
        payment = paymentRepository.save(payment);
        try {
            OrderDto orderDto = orderFeign.successOrderPayment(payment.getOrderId());
            log.trace("Order state changed to paid: {}", orderDto);
        } catch (FeignException e) {
            throw new RuntimeException(e.getMessage());
        }
        log.debug("Payment state in payment with id = {} changed to success, {}", paymentId, payment);
    }

    @Override
    public BigDecimal calculateProductCost(OrderDto orderDto) {
        log.info("Starting to calculate product cost");
        Map<UUID, Long> products = orderDto.products();
        if (products.isEmpty()) {
            log.warn("Not enough info in order to calculate in order {}", orderDto);
            throw new NotEnoughInfoInOrderToCalculateException("Not enough info in order to calculate");
        }
        List<ProductDto> productsList;
        try {
            productsList = shoppingStoreFeign.getProductsById(products.keySet().stream().toList());
            log.trace("Product list received: {}", productsList);
        } catch (FeignException e) {
            throw new RuntimeException(e.getMessage());
        }
        BigDecimal productCost = BigDecimal.valueOf(0.0);
        for (ProductDto product : productsList) {
            BigDecimal price = product.price();
            Long quantity = products.get(product.productId());
            productCost = productCost.add(price.multiply(BigDecimal.valueOf(quantity)));
        }
        log.debug("Product cost for order with id = {} calculated, {}", orderDto.orderId(), productCost);
        return productCost;
    }

    @Override
    public void failedPayment(UUID paymentId) {
        log.info("Starting to change payment state to failed");
        Payment payment = getPaymentById(paymentId);
        payment.setPaymentState(PaymentState.FAILED);
        payment = paymentRepository.save(payment);
        try {
            OrderDto orderDto = orderFeign.failedOrderPayment(payment.getOrderId());
            log.trace("Order state changed to payment failed: {}", orderDto);
        } catch (FeignException e) {
            throw new RuntimeException(e.getMessage());
        }
        log.debug("Payment state in payment with id = {} changed, {}", paymentId, payment);
    }

    private Payment getPaymentById(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NoPaymentFoundException("Payment with id " + paymentId + " not found"));
    }
}
