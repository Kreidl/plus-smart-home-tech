package ru.yandex.practicum.cart.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.UUID;

public record ShoppingCartDto (@NotNull UUID cartId, @NotNull Map<UUID, Long> products) {}
