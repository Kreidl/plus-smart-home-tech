package ru.yandex.practicum.order.dto;

import jakarta.validation.constraints.NotNull;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.warehouse.dto.AddressDto;

public record CreateNewOrderRequest(@NotNull ShoppingCartDto shoppingCartDto, @NotNull AddressDto addressDto) {}
