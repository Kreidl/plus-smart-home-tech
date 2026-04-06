package ru.yandex.practicum.warehouse.dto;

import java.util.UUID;

public record ShippedToDeliveryRequest (UUID orderId, UUID deliveryId) {
}
