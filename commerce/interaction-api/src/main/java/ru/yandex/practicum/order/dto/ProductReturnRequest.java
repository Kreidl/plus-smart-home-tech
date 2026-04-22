package ru.yandex.practicum.order.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.UUID;

public record ProductReturnRequest(UUID orderId, @NotNull Map<UUID, Long> products) {}
