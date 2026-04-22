package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.feign.OrderFeign;
import ru.yandex.practicum.order.dto.CreateNewOrderRequest;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.dto.ProductReturnRequest;
import ru.yandex.practicum.service.OrderService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController implements OrderFeign {
    private final OrderService orderService;

    @Override
    @ResponseStatus(HttpStatus.OK)
    public List<OrderDto> getAllUserOrders(String username) {
        log.info("New request to get all user orders with username = {}", username);
        return orderService.getAllUserOrders(username);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public OrderDto createOrder(CreateNewOrderRequest createNewOrderRequest) {
        log.info("New request to create order, {}", createNewOrderRequest);
        return orderService.createOrder(createNewOrderRequest);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public OrderDto returnOrder(ProductReturnRequest productReturnRequest) {
        log.info("New request to return products, {}", productReturnRequest);
        return orderService.returnOrder(productReturnRequest);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public OrderDto orderPayment(UUID orderId) {
        log.info("New request to change order state to on payment in order with id = {}", orderId);
        return orderService.orderPayment(orderId);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public OrderDto successOrderPayment(UUID orderId) {
        log.info("New request to change order state to paid in order with id = {}", orderId);
        return orderService.successOrderPayment(orderId);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public OrderDto failedOrderPayment(UUID orderId) {
        log.info("New request to change order state to payment failed in order with id = {}", orderId);
        return orderService.failedOrderPayment(orderId);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public OrderDto orderDelivery(UUID orderId) {
        log.info("New request to change order state to delivered in order with id = {}", orderId);
        return orderService.orderDelivery(orderId);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public OrderDto failedOrderDelivery(UUID orderId) {
        log.info("New request to change order state to delivery failed in order with id = {}", orderId);
        return orderService.failedOrderDelivery(orderId);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public OrderDto completedOrder(UUID orderId) {
        log.info("New request to change order state to completed in order with id = {}", orderId);
        return orderService.completedOrder(orderId);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public OrderDto calculateTotalCost(UUID orderId) {
        log.info("New request to calculate total cost in order with id = {}", orderId);
        return orderService.calculateTotalCost(orderId);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public OrderDto calculateDeliveryCost(UUID orderId) {
        log.info("New request to calculate delivery cost in order with id = {}", orderId);
        return orderService.calculateDeliveryCost(orderId);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public OrderDto orderAssembly(UUID orderId) {
        log.info("New request to change order state to assembled in order with id = {}", orderId);
        return orderService.orderAssembly(orderId);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public OrderDto failedOrderAssembly(UUID orderId) {
        log.info("New request to change order state to assembly failed in order with id = {}", orderId);
        return orderService.failedOrderAssembly(orderId);
    }
}
