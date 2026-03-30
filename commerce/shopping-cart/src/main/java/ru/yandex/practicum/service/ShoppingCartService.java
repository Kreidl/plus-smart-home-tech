package ru.yandex.practicum.service;

import ru.yandex.practicum.cart.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ShoppingCartService {
    ShoppingCartDto getCartByUsername(String username);

    ShoppingCartDto addProductsToCart(String username, Map<UUID, Long> newProducts);

    void deactivateCart(String username);

    ShoppingCartDto removeProductsFromCart(String username, List<UUID> products);

    ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request);
}
