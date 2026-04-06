package ru.yandex.practicum.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.delivery.dto.DeliveryDto;
import ru.yandex.practicum.delivery.enums.DeliveryState;
import ru.yandex.practicum.exception.model.NoOrderFoundException;
import ru.yandex.practicum.exception.model.UserAuthorizationException;
import ru.yandex.practicum.feign.DeliveryFeign;
import ru.yandex.practicum.feign.PaymentFeign;
import ru.yandex.practicum.feign.ShoppingCartFeign;
import ru.yandex.practicum.feign.WarehouseFeign;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.order.dto.CreateNewOrderRequest;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.dto.ProductReturnRequest;
import ru.yandex.practicum.order.enums.OrderState;
import ru.yandex.practicum.payment.dto.PaymentDto;
import ru.yandex.practicum.repository.OrderRepository;
import ru.yandex.practicum.warehouse.dto.AddressDto;
import ru.yandex.practicum.warehouse.dto.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.warehouse.dto.BookedProductsDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final WarehouseFeign warehouseFeign;
    private final ShoppingCartFeign shoppingCartFeign;
    private final PaymentFeign paymentFeign;
    private final DeliveryFeign deliveryFeign;

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getAllUserOrders(String username) {
        checkUserAuthorization(username);
        ShoppingCartDto shoppingCartDto = shoppingCartFeign.getCartByUsername(username);
        return orderRepository.findByShoppingCartId(shoppingCartDto.cartId()).stream()
                .map(OrderMapper::mapToDto)
                .toList();
    }

    @Override
    public OrderDto createOrder(CreateNewOrderRequest createNewOrderRequest) {
        BookedProductsDto bookedProductsDto;
        try {
            bookedProductsDto = warehouseFeign.checkCart(createNewOrderRequest.shoppingCartDto());
        } catch (FeignException e) {
            throw new RuntimeException(e.getMessage());
        }
        Order order = OrderMapper.mapToEntity(createNewOrderRequest, bookedProductsDto);
        order = orderRepository.save(order);

        AddressDto warehouseAddress = warehouseFeign.getWarehouseAddress();
        DeliveryDto deliveryDto = new DeliveryDto(null, warehouseAddress, createNewOrderRequest.addressDto(),
                order.getOrderId(), DeliveryState.CREATED);
        deliveryDto = deliveryFeign.planDelivery(deliveryDto);
        order.setDeliveryId(deliveryDto.deliveryId());

        OrderDto orderDto = OrderMapper.mapToDto(order);
        BigDecimal deliveryCost = deliveryFeign.calculateDeliveryCost(orderDto);
        order.setDeliveryPrice(deliveryCost);

        BigDecimal productCost = paymentFeign.calculateProductCost(orderDto);
        order.setProductPrice(productCost);

        orderDto = OrderMapper.mapToDto(order);
        BigDecimal totalCost = paymentFeign.calculateTotalCost(orderDto);
        order.setTotalPrice(totalCost);

        return OrderMapper.mapToDto(orderRepository.save(order));
    }

    @Override
    public OrderDto returnOrder(ProductReturnRequest productReturnRequest) {
        Order order = getOrderById(productReturnRequest.orderId());
        Map<UUID, Long> products = order.getProducts();
        warehouseFeign.returnProducts(products);
        order.setState(OrderState.PRODUCT_RETURNED);
        return OrderMapper.mapToDto(orderRepository.save(order));
    }

    @Override
    public OrderDto orderPayment(UUID orderId) {
        Order order = getOrderById(orderId);
        PaymentDto paymentDto = paymentFeign.createPayment(OrderMapper.mapToDto(order));
        order.setPaymentId(paymentDto.paymentId());
        return OrderMapper.mapToDto(setOrderStateAndSave(order, OrderState.ON_PAYMENT));
    }

    @Override
    public OrderDto successOrderPayment(UUID orderId) {
        Order order = getOrderById(orderId);
        return OrderMapper.mapToDto(setOrderStateAndSave(order, OrderState.PAID));
    }

    @Override
    public OrderDto failedOrderPayment(UUID orderId) {
        Order order = getOrderById(orderId);
        return OrderMapper.mapToDto(setOrderStateAndSave(order, OrderState.PAYMENT_FAILED));
    }

    @Override
    public OrderDto orderDelivery(UUID orderId) {
        Order order = getOrderById(orderId);
        return OrderMapper.mapToDto(setOrderStateAndSave(order, OrderState.DELIVERED));
    }

    @Override
    public OrderDto failedOrderDelivery(UUID orderId) {
        Order order = getOrderById(orderId);
        return OrderMapper.mapToDto(setOrderStateAndSave(order, OrderState.DELIVERY_FAILED));
    }

    @Override
    public OrderDto completedOrder(UUID orderId) {
        Order order = getOrderById(orderId);
        return OrderMapper.mapToDto(setOrderStateAndSave(order, OrderState.COMPLETED));
    }

    @Override
    public OrderDto calculateTotalCost(UUID orderId) {
        Order order = getOrderById(orderId);
        order.setTotalPrice(paymentFeign.calculateTotalCost(OrderMapper.mapToDto(order)));
        return OrderMapper.mapToDto(orderRepository.save(order));
    }

    @Override
    public OrderDto calculateDeliveryCost(UUID orderId) {
        Order order = getOrderById(orderId);
        order.setDeliveryPrice(deliveryFeign.calculateDeliveryCost(OrderMapper.mapToDto(order)));
        return OrderMapper.mapToDto(orderRepository.save(order));
    }

    @Override
    public OrderDto orderAssembly(UUID orderId) {
        Order order = getOrderById(orderId);
        warehouseFeign.assemblyProductForOrderFromShoppingCart(new AssemblyProductsForOrderRequest(order.getProducts(),
                orderId));
        return OrderMapper.mapToDto(setOrderStateAndSave(order, OrderState.ASSEMBLED));
    }

    @Override
    public OrderDto failedOrderAssembly(UUID orderId) {
        Order order = getOrderById(orderId);
        return OrderMapper.mapToDto(setOrderStateAndSave(order, OrderState.ASSEMBLY_FAILED));
    }

    private void checkUserAuthorization(String username) {
        log.info("Start checking user authorization");
        if (username.isBlank()) {
            log.warn("Username cannot be blank");
            throw new UserAuthorizationException("Username cannot be blank");
        }
    }

    private Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Order with id = " + orderId + " not found"));
    }

    private Order setOrderStateAndSave(Order order, OrderState orderState) {
        order.setState(orderState);
        order = orderRepository.save(order);
        return order;
    }
}
