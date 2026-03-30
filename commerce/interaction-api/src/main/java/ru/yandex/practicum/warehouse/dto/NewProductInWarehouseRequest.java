package ru.yandex.practicum.warehouse.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record NewProductInWarehouseRequest (@NotNull UUID productId, @NotNull DimensionDto dimension,
                                            Boolean fragile, @NotNull @Min(1) Double weight) {}
