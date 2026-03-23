package ru.yandex.practicum.store.dto;

import jakarta.validation.constraints.NotNull;
import ru.yandex.practicum.store.enums.QuantityState;

import java.util.UUID;

public record SetProductQuantityState(@NotNull UUID productId,
                                      @NotNull QuantityState quantityState) {}
