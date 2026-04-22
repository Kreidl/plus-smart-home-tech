package ru.yandex.practicum.warehouse.dto;

import java.util.Map;
import java.util.UUID;

public record AssemblyProductsForOrderRequest(Map<UUID, Long> products, UUID orderId) {
}
