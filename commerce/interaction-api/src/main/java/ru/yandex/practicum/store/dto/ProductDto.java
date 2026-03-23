package ru.yandex.practicum.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.yandex.practicum.store.enums.ProductCategory;
import ru.yandex.practicum.store.enums.ProductState;
import ru.yandex.practicum.store.enums.QuantityState;

import java.util.UUID;

public record ProductDto(UUID id, @NotBlank String name, @NotBlank String description, @NotBlank String imageSrc,
                         @NotNull QuantityState quantityState, @NotNull ProductState productState,
                         @NotNull ProductCategory productCategory, @NotNull Float price) {}
