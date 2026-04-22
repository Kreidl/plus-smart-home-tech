package ru.yandex.practicum.order.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import ru.yandex.practicum.order.enums.OrderState;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public record OrderDto(@NotNull UUID orderId, @Nullable UUID shoppingCartId, @NotNull Map<UUID, Long> products,
                       UUID paymentId, UUID deliveryId, OrderState state, Double deliveryWeight, Double deliveryVolume,
                       Boolean fragile, BigDecimal totalPrice, BigDecimal deliveryPrice, BigDecimal productPrice) {}
