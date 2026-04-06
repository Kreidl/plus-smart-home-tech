package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController implements OrderFeign {
    private final OrderService orderService;


    @Override
    @ResponseStatus(HttpStatus.OK)
    public List<OrderDto> getAllUserOrders(String username) {
        return orderService.getAllUserOrders(username);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public OrderDto createOrder(CreateNewOrderRequest createNewOrderRequest) {
        return orderService.createOrder(createNewOrderRequest);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public OrderDto returnOrder(ProductReturnRequest productReturnRequest) {
        return orderService.returnOrder(productReturnRequest);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public OrderDto orderPayment(UUID orderId) {
        return orderService.orderPayment(orderId);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public OrderDto successOrderPayment(UUID orderId) {
        return orderService.successOrderPayment(orderId);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public OrderDto failedOrderPayment(UUID orderId) {
        return orderService.failedOrderPayment(orderId);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public OrderDto orderDelivery(UUID orderId) {
        return orderService.orderDelivery(orderId);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public OrderDto failedOrderDelivery(UUID orderId) {
        return orderService.failedOrderDelivery(orderId);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public OrderDto completedOrder(UUID orderId) {
        return orderService.completedOrder(orderId);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public OrderDto calculateTotalCost(UUID orderId) {
        return orderService.calculateTotalCost(orderId);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public OrderDto calculateDeliveryCost(UUID orderId) {
        return orderService.calculateDeliveryCost(orderId);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public OrderDto orderAssembly(UUID orderId) {
        return orderService.orderAssembly(orderId);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public OrderDto failedOrderAssembly(UUID orderId) {
        return orderService.failedOrderAssembly(orderId);
    }
}
