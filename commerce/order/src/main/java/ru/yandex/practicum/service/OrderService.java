package ru.yandex.practicum.service;

import ru.yandex.practicum.order.dto.CreateNewOrderRequest;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.dto.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    List<OrderDto> getAllUserOrders(String username);

    OrderDto createOrder(CreateNewOrderRequest createNewOrderRequest);

    OrderDto returnOrder(ProductReturnRequest productReturnRequest);

    OrderDto orderPayment(UUID orderId);

    OrderDto successOrderPayment(UUID orderId);

    OrderDto failedOrderPayment(UUID orderId);

    OrderDto orderDelivery(UUID orderId);

    OrderDto failedOrderDelivery(UUID orderId);

    OrderDto completedOrder(UUID orderId);

    OrderDto calculateTotalCost(UUID orderId);

    OrderDto calculateDeliveryCost(UUID orderId);

    OrderDto orderAssembly(UUID orderId);

    OrderDto failedOrderAssembly(UUID orderId);
}
