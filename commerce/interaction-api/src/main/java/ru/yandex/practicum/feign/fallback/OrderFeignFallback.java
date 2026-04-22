package ru.yandex.practicum.feign.fallback;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.feign.OrderFeign;
import ru.yandex.practicum.order.dto.CreateNewOrderRequest;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.dto.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class OrderFeignFallback implements OrderFeign {
    @Override
    public List<OrderDto> getAllUserOrders(String username) throws FeignException {
        fallback();
        return List.of();
    }

    @Override
    public OrderDto createOrder(CreateNewOrderRequest createNewOrderRequest) throws FeignException {
        fallback();
        return null;
    }

    @Override
    public OrderDto returnOrder(ProductReturnRequest productReturnRequest) throws FeignException {
        fallback();
        return null;
    }

    @Override
    public OrderDto orderPayment(UUID orderId) throws FeignException {
        fallback();
        return null;
    }

    @Override
    public OrderDto failedOrderPayment(UUID orderId) throws FeignException {
        fallback();
        return null;
    }

    @Override
    public OrderDto orderDelivery(UUID orderId) throws FeignException {
        fallback();
        return null;
    }

    @Override
    public OrderDto failedOrderDelivery(UUID orderId) throws FeignException {
        fallback();
        return null;
    }

    @Override
    public OrderDto completedOrder(UUID orderId) throws FeignException {
        fallback();
        return null;
    }

    @Override
    public OrderDto calculateTotalCost(UUID orderId) throws FeignException {
        fallback();
        return null;
    }

    @Override
    public OrderDto calculateDeliveryCost(UUID orderId) throws FeignException {
        fallback();
        return null;
    }

    @Override
    public OrderDto orderAssembly(UUID orderId) throws FeignException {
        fallback();
        return null;
    }

    @Override
    public OrderDto failedOrderAssembly(UUID orderId) throws FeignException {
        fallback();
        return null;
    }

    @Override
    public OrderDto successOrderPayment(UUID orderId) {
        fallback();
        return null;
    }

    private void fallback() {
        log.error("Fallback: order service is not responding");
        throw new RuntimeException("Order service is not responding, please try again later");
    }
}
