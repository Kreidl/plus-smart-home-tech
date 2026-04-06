package ru.yandex.practicum.feign;

import feign.FeignException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.feign.fallback.OrderFeignFallback;
import ru.yandex.practicum.order.dto.CreateNewOrderRequest;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.dto.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "order", path = "/api/v1/order",
        fallback = OrderFeignFallback.class)
public interface OrderFeign {
    @GetMapping
    List<OrderDto> getAllUserOrders(@RequestParam(name = "username") String username) throws FeignException;

    @PutMapping
    OrderDto createOrder(@Valid @RequestBody CreateNewOrderRequest createNewOrderRequest) throws FeignException;

    @PostMapping("/return")
    OrderDto returnOrder(@Valid @RequestBody ProductReturnRequest productReturnRequest) throws FeignException;

    @PostMapping("/payment")
    OrderDto orderPayment(@NotNull @RequestBody UUID orderId) throws FeignException;

    @PostMapping("/payment/failed")
    OrderDto failedOrderPayment(@NotNull @RequestBody UUID orderId) throws FeignException;

    @PostMapping("/delivery")
    OrderDto orderDelivery(@NotNull @RequestBody UUID orderId) throws FeignException;

    @PostMapping("/delivery/failed")
    OrderDto failedOrderDelivery(@NotNull @RequestBody UUID orderId) throws FeignException;

    @PostMapping("/completed")
    OrderDto completedOrder(@NotNull @RequestBody UUID orderId) throws FeignException;

    @PostMapping("/calculate/total")
    OrderDto calculateTotalCost(@NotNull @RequestBody UUID orderId) throws FeignException;

    @PostMapping("/calculate/delivery")
    OrderDto calculateDeliveryCost(@NotNull @RequestBody UUID orderId) throws FeignException;

    @PostMapping("/assembly")
    OrderDto orderAssembly(@NotNull @RequestBody UUID orderId) throws FeignException;

    @PostMapping("/assembly/failed")
    OrderDto failedOrderAssembly(@NotNull @RequestBody UUID orderId) throws FeignException;

    @PostMapping("/api/v1/order/payment/success")
    OrderDto successOrderPayment(@NotNull @RequestBody UUID orderId);
}
