package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.cart.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.feign.ShoppingCartFeign;
import ru.yandex.practicum.service.ShoppingCartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-cart")
public class ShoppingCartController implements ShoppingCartFeign {
    private final ShoppingCartService shoppingCartService;

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartDto getCartByUsername(String username) {
        log.info("New request from {} to get cart by name", username);
        return shoppingCartService.getCartByUsername(username);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartDto addProductInCart(String username, Map<UUID, Long> products) {
        log.info("New request from {} to add product in cart {}", username, products);
        return shoppingCartService.addProductsToCart(username, products);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public void deactivateCart(String username) {
        log.info("New request from {} to deactivate cart", username);
        shoppingCartService.deactivateCart(username);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartDto removeProductsFromCart(String username, List<UUID> products) {
        log.info("New request from user {} to remove products from cart {}", username, products);
        return shoppingCartService.removeProductsFromCart(username, products);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        log.info("New request from {} to change product quantity in cart {}", username, request);
        return shoppingCartService.changeProductQuantity(username, request);
    }
}
