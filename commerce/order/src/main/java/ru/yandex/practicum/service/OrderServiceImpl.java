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
        log.info("Starting to get all user orders");
        checkUserAuthorization(username);
        ShoppingCartDto shoppingCartDto = shoppingCartFeign.getCartByUsername(username);
        log.trace("Shopping cart received: {}", shoppingCartDto);
        return orderRepository.findByShoppingCartId(shoppingCartDto.cartId()).stream()
                .map(OrderMapper::mapToDto)
                .toList();
    }

    @Override
    public OrderDto createOrder(CreateNewOrderRequest createNewOrderRequest) {
        log.info("Starting to create order");
        BookedProductsDto bookedProductsDto;
        try {
            bookedProductsDto = warehouseFeign.checkCart(createNewOrderRequest.shoppingCartDto());
            log.trace("Cart checked, booked products: {}", bookedProductsDto);
        } catch (FeignException e) {
            throw new RuntimeException(e.getMessage());
        }
        Order order = OrderMapper.mapToEntity(createNewOrderRequest, bookedProductsDto);
        order = orderRepository.save(order);

        AddressDto warehouseAddress = warehouseFeign.getWarehouseAddress();
        log.trace("Warehouse address received: {}", warehouseAddress);
        DeliveryDto deliveryDto = new DeliveryDto(null, warehouseAddress, createNewOrderRequest.addressDto(),
                order.getOrderId(), DeliveryState.CREATED);
        deliveryDto = deliveryFeign.planDelivery(deliveryDto);
        log.trace("Delivery planed: {}", deliveryDto);

        order.setDeliveryId(deliveryDto.deliveryId());

        OrderDto orderDto = OrderMapper.mapToDto(order);
        BigDecimal deliveryCost = deliveryFeign.calculateDeliveryCost(orderDto);
        log.trace("Delivery cost calculated: {}", deliveryCost);
        order.setDeliveryPrice(deliveryCost);

        BigDecimal productCost = paymentFeign.calculateProductCost(orderDto);
        log.trace("Product cost calculated: {}", productCost);
        order.setProductPrice(productCost);

        orderDto = OrderMapper.mapToDto(order);
        BigDecimal totalCost = paymentFeign.calculateTotalCost(orderDto);
        log.trace("Total cost calculated: {}", totalCost);
        order.setTotalPrice(totalCost);

        order = orderRepository.save(order);
        log.info("Order created, {}", order);
        return OrderMapper.mapToDto(order);
    }

    @Override
    public OrderDto returnOrder(ProductReturnRequest productReturnRequest) {
        log.info("Starting to return products");
        Order order = getOrderById(productReturnRequest.orderId());
        Map<UUID, Long> products = order.getProducts();
        warehouseFeign.returnProducts(products);
        log.trace("Products returned in warehouse");
        order = setOrderStateAndSave(order, OrderState.PRODUCT_RETURNED);
        log.info("Products returned from order, {}", order);
        return OrderMapper.mapToDto(order);
    }

    @Override
    public OrderDto orderPayment(UUID orderId) {
        log.info("Starting to change order state to on payment");
        Order order = getOrderById(orderId);
        PaymentDto paymentDto = paymentFeign.createPayment(OrderMapper.mapToDto(order));
        log.trace("Payment created: {}", paymentDto);
        order.setPaymentId(paymentDto.paymentId());
        order = setOrderStateAndSave(order, OrderState.ON_PAYMENT);
        return OrderMapper.mapToDto(order);
    }

    @Override
    public OrderDto successOrderPayment(UUID orderId) {
        log.info("Starting to change order state to paid");
        Order order = getOrderById(orderId);
        order = setOrderStateAndSave(order, OrderState.PAID);
        return OrderMapper.mapToDto(order);
    }

    @Override
    public OrderDto failedOrderPayment(UUID orderId) {
        log.info("Starting to change order state to payment failed");
        Order order = getOrderById(orderId);
        order = setOrderStateAndSave(order, OrderState.PAYMENT_FAILED);
        return OrderMapper.mapToDto(order);
    }

    @Override
    public OrderDto orderDelivery(UUID orderId) {
        log.info("Starting to change order state to delivered");
        Order order = getOrderById(orderId);
        order = setOrderStateAndSave(order, OrderState.DELIVERED);
        return OrderMapper.mapToDto(order);
    }

    @Override
    public OrderDto failedOrderDelivery(UUID orderId) {
        log.info("Starting to change order state to delivery failed");
        Order order = getOrderById(orderId);
        order = setOrderStateAndSave(order, OrderState.DELIVERY_FAILED);
        return OrderMapper.mapToDto(order);
    }

    @Override
    public OrderDto completedOrder(UUID orderId) {
        log.info("Starting to change order state to completed");
        Order order = getOrderById(orderId);
        order = setOrderStateAndSave(order, OrderState.COMPLETED);
        return OrderMapper.mapToDto(order);
    }

    @Override
    public OrderDto calculateTotalCost(UUID orderId) {
        log.info("Starting to calculate total cost");
        Order order = getOrderById(orderId);
        order.setTotalPrice(paymentFeign.calculateTotalCost(OrderMapper.mapToDto(order)));
        log.debug("Total price in order with id = {} calculated: {}", orderId, order.getTotalPrice());
        return OrderMapper.mapToDto(orderRepository.save(order));
    }

    @Override
    public OrderDto calculateDeliveryCost(UUID orderId) {
        log.info("Starting to calculate delivery cost");
        Order order = getOrderById(orderId);
        order.setDeliveryPrice(deliveryFeign.calculateDeliveryCost(OrderMapper.mapToDto(order)));
        log.debug("Delivery price in order with id = {} calculated: {}", orderId, order.getDeliveryPrice());
        return OrderMapper.mapToDto(orderRepository.save(order));
    }

    @Override
    public OrderDto orderAssembly(UUID orderId) {
        log.info("Starting to change order state to assembled");
        Order order = getOrderById(orderId);
        BookedProductsDto bookedProductsDto = warehouseFeign.assemblyProductForOrderFromShoppingCart
                (new AssemblyProductsForOrderRequest(order.getProducts(), orderId));
        log.trace("Assembly products for order requested, booked products: {}", bookedProductsDto);
        order = setOrderStateAndSave(order, OrderState.ASSEMBLED);
        return OrderMapper.mapToDto(order);
    }

    @Override
    public OrderDto failedOrderAssembly(UUID orderId) {
        log.info("Starting to change order state to assembly failed");
        Order order = getOrderById(orderId);
        order = setOrderStateAndSave(order, OrderState.ASSEMBLY_FAILED);
        return OrderMapper.mapToDto(order);
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
        log.info("Order state changed, {}", order);
        return order;
    }
}
