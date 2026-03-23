package ru.yandex.practicum.warehouse.dto;

import jakarta.validation.constraints.NotNull;

public record AddressDto (@NotNull String country, @NotNull String city, @NotNull String street,
                          @NotNull String house, @NotNull String flat) {}
