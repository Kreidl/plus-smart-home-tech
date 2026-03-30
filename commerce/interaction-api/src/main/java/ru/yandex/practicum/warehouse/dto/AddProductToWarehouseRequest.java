package ru.yandex.practicum.warehouse.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddProductToWarehouseRequest (@NotNull UUID productId, @NotNull @Min(1) Long quantity) {}
