package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.delivery.dto.DeliveryDto;
import ru.yandex.practicum.delivery.enums.DeliveryState;
import ru.yandex.practicum.exception.model.NoDeliveryFoundException;
import ru.yandex.practicum.feign.OrderFeign;
import ru.yandex.practicum.feign.WarehouseFeign;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.repository.DeliveryRepository;
import ru.yandex.practicum.warehouse.dto.ShippedToDeliveryRequest;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final OrderFeign orderFeign;
    private final WarehouseFeign warehouseFeign;
    private static final BigDecimal BASE_COST_OF_DELIVERY = BigDecimal.valueOf(5.0);
    private static final BigDecimal WAREHOUSE_ADDRESS_1_MARKUP = BigDecimal.valueOf(1.0);
    private static final BigDecimal WAREHOUSE_ADDRESS_2_MARKUP = BigDecimal.valueOf(2.0);
    private static final BigDecimal FRAGILE_MARKUP = BigDecimal.valueOf(0.2);
    private static final BigDecimal WEIGHT_MARKUP = BigDecimal.valueOf(0.3);
    private static final BigDecimal VOLUME_MARKUP = BigDecimal.valueOf(0.2);
    private static final BigDecimal STREET_DIFFERENT_FROM_WAREHOUSE_MARKUP = BigDecimal.valueOf(0.2);


    @Override
    public DeliveryDto planDelivery(DeliveryDto deliveryDto) {
        log.info("Starting to plan delivery");
        Delivery delivery = DeliveryMapper.mapToEntity(deliveryDto);
        delivery.setDeliveryState(DeliveryState.CREATED);
        delivery = deliveryRepository.save(delivery);
        log.debug("Delivery planed, {}", delivery);
        return DeliveryMapper.mapToDto(delivery);
    }

    @Override
    public void successfulDelivery(UUID orderId) {
        log.info("Starting to change delivery state to success");
        Delivery delivery = getDeliveryByOrderId(orderId);
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        deliveryRepository.save(delivery);
        OrderDto orderDto = orderFeign.orderDelivery(orderId);
        log.trace("Order state changed to delivered, {}", orderDto);
        log.debug("Delivery state changed to success: {}", delivery);
    }

    @Override
    public void pickedDelivery(UUID orderId) {
        log.info("Starting to change delivery state to in progress");
        Delivery delivery = getDeliveryByOrderId(orderId);
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        delivery = deliveryRepository.save(delivery);
        OrderDto orderDto = orderFeign.orderAssembly(orderId);
        log.trace("Order state changed to assembled, {}", orderDto);
        warehouseFeign.shippedToDelivery(new ShippedToDeliveryRequest(orderId, delivery.getDeliveryId()));
        log.trace("Order shipped to delivery");
        log.debug("Delivery state changed to in progress: {}", delivery);
    }

    @Override
    public void failedDelivery(UUID orderId) {
        log.info("Starting to change delivery state to failed");
        Delivery delivery = getDeliveryByOrderId(orderId);
        delivery.setDeliveryState(DeliveryState.FAILED);
        deliveryRepository.save(delivery);
        OrderDto orderDto = orderFeign.failedOrderDelivery(orderId);
        log.trace("Order state changed to failed delivery, {}", orderDto);
        log.debug("Delivery state changed to failed: {}", delivery);
    }

    @Override
    public BigDecimal calculateDeliveryCost(OrderDto orderDto) {
        log.info("Starting to calculate delivery cost");
        Delivery delivery = getDeliveryById(orderDto.deliveryId());
        Address fromAddress = delivery.getFromAddress();
        Address toAddress = delivery.getToAddress();
        BigDecimal totalCost = BASE_COST_OF_DELIVERY;
        log.trace("Base delivery cost = {}", totalCost);

        totalCost = fromAddress.getCountry().equals("ADDRESS_1") ?
                totalCost.add(totalCost.multiply(WAREHOUSE_ADDRESS_1_MARKUP)) :
                totalCost.add(totalCost.multiply(WAREHOUSE_ADDRESS_2_MARKUP));
        log.trace("Added markup for address = {}, totalCost = {}", fromAddress.getCountry(), totalCost);

        totalCost = orderDto.fragile() ? totalCost.add(totalCost.multiply(FRAGILE_MARKUP)) : totalCost;
        log.trace("Added markup for fragile = {}, totalCost = {}", orderDto.fragile(), totalCost);

        totalCost = totalCost.add(BigDecimal.valueOf(orderDto.deliveryWeight()).multiply(WEIGHT_MARKUP));
        log.trace("Added markup for delivery weight = {}, totalCost = {}", orderDto.deliveryWeight(), totalCost);

        totalCost = totalCost.add(BigDecimal.valueOf(orderDto.deliveryVolume()).multiply(VOLUME_MARKUP));
        log.trace("Added markup for delivery volume = {}, totalCost = {}", orderDto.deliveryVolume(), totalCost);

        totalCost = fromAddress.getStreet().equals(toAddress.getStreet()) ?
                totalCost : totalCost.add(totalCost.multiply(STREET_DIFFERENT_FROM_WAREHOUSE_MARKUP));
        log.trace("Added markup for delivery street = {}, totalCost = {}",
                fromAddress.getStreet().equals(toAddress.getStreet()), totalCost);

        log.debug("Delivery cost in order with id = {} calculated: {}", orderDto.orderId(), totalCost);
        return totalCost;
    }

    private Delivery getDeliveryByOrderId(UUID orderId) {
        return deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NoDeliveryFoundException("Delivery with order id" + orderId + " not found"));
    }

    private Delivery getDeliveryById(UUID deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NoDeliveryFoundException("Delivery with id" + deliveryId + " not found"));
    }
}
