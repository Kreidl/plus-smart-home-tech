package ru.yandex.practicum.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ru.yandex.practicum.store.enums.ProductCategory;
import ru.yandex.practicum.store.enums.ProductState;
import ru.yandex.practicum.store.enums.QuantityState;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductDto(UUID productId, @NotBlank String productName, @NotBlank String description,
                         @NotBlank String imageSrc, @NotNull QuantityState quantityState,
                         @NotNull ProductState productState, @NotNull ProductCategory productCategory,
                         @NotNull @Positive BigDecimal price) {}
