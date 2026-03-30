package ru.yandex.practicum.warehouse.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record DimensionDto(@NotNull @Min(1) Double width, @NotNull @Min(1) Double height,
                           @NotNull @Min(1) Double depth) {}
