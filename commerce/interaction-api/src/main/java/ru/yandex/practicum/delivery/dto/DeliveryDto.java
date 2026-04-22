package ru.yandex.practicum.delivery.dto;

import jakarta.validation.constraints.NotNull;
import ru.yandex.practicum.delivery.enums.DeliveryState;
import ru.yandex.practicum.warehouse.dto.AddressDto;

import java.util.UUID;

public record DeliveryDto(@NotNull UUID deliveryId, @NotNull AddressDto fromAddressDto,
                          @NotNull AddressDto toAddressDto, @NotNull UUID orderId,
                          @NotNull DeliveryState deliveryState) {
}
