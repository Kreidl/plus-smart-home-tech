package ru.yandex.practicum.cart.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ChangeProductQuantityRequest (@NotNull UUID productId, @NotNull  Long newQuantity) {}
