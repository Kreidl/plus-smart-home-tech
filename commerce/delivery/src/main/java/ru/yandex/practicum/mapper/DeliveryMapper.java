package ru.yandex.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.delivery.dto.DeliveryDto;
import ru.yandex.practicum.model.Delivery;

@UtilityClass
public class DeliveryMapper {
    public static Delivery mapToEntity(DeliveryDto deliveryDto) {
        return new Delivery(deliveryDto.deliveryId(), AddressMapper.mapToEntity(deliveryDto.fromAddressDto()),
                AddressMapper.mapToEntity(deliveryDto.toAddressDto()), deliveryDto.orderId(),
                deliveryDto.deliveryState());
    }

    public static DeliveryDto mapToDto(Delivery delivery) {
        return new DeliveryDto(delivery.getDeliveryId(), AddressMapper.mapToDto(delivery.getFromAddress()),
                AddressMapper.mapToDto(delivery.getToAddress()), delivery.getOrderId(), delivery.getDeliveryState());
    }
}
