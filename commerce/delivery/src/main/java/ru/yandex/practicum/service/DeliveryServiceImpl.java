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
        Delivery delivery = DeliveryMapper.mapToEntity(deliveryDto);
        delivery.setDeliveryState(DeliveryState.CREATED);
        return DeliveryMapper.mapToDto(deliveryRepository.save(delivery));
    }

    @Override
    public void successfulDelivery(UUID orderId) {
        Delivery delivery = getDeliveryByOrderId(orderId);
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        deliveryRepository.save(delivery);
        orderFeign.orderDelivery(orderId);
    }

    @Override
    public void pickedDelivery(UUID orderId) {
        Delivery delivery = getDeliveryByOrderId(orderId);
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        delivery = deliveryRepository.save(delivery);
        orderFeign.orderAssembly(orderId);
        warehouseFeign.shippedToDelivery(new ShippedToDeliveryRequest(orderId, delivery.getDeliveryId()));
    }

    @Override
    public void failedDelivery(UUID orderId) {
        Delivery delivery = getDeliveryByOrderId(orderId);
        delivery.setDeliveryState(DeliveryState.FAILED);
        deliveryRepository.save(delivery);
        orderFeign.failedOrderDelivery(orderId);
    }

    @Override
    public BigDecimal calculateDeliveryCost(OrderDto orderDto) {
        Delivery delivery = getDeliveryById(orderDto.deliveryId());
        Address fromAddress = delivery.getFromAddress();
        Address toAddress = delivery.getToAddress();
        BigDecimal totalCost = BASE_COST_OF_DELIVERY;
        totalCost = fromAddress.getCountry().equals("ADDRESS_1") ?
                totalCost.add(totalCost.multiply(WAREHOUSE_ADDRESS_1_MARKUP)) :
                totalCost.add(totalCost.multiply(WAREHOUSE_ADDRESS_2_MARKUP));
        totalCost = orderDto.fragile() ? totalCost.add(totalCost.multiply(FRAGILE_MARKUP)) : totalCost;
        totalCost = totalCost.add(BigDecimal.valueOf(orderDto.deliveryWeight()).multiply(WEIGHT_MARKUP));
        totalCost = totalCost.add(BigDecimal.valueOf(orderDto.deliveryVolume()).multiply(VOLUME_MARKUP));
        totalCost = fromAddress.getStreet().equals(toAddress.getStreet()) ?
                totalCost : totalCost.add(totalCost.multiply(STREET_DIFFERENT_FROM_WAREHOUSE_MARKUP));
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
