package ru.yandex.practicum.feign.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.cart.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.feign.ShoppingCartFeign;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class ShoppingCartFeignFallback implements ShoppingCartFeign {
    @Override
    public ShoppingCartDto getCartByUsername(String username) {
        fallback();
        return null;
    }

    @Override
    public ShoppingCartDto addProductInCart(String username, Map<UUID, Long> products) {
        fallback();
        return null;
    }

    @Override
    public void deactivateCart(String username) {
        fallback();
    }

    @Override
    public ShoppingCartDto removeProductsFromCart(String username, List<UUID> products) {
        fallback();
        return null;
    }

    @Override
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        fallback();
        return null;
    }

    private void fallback() {
        log.error("Fallback: shopping-store service is not responding");
        throw new RuntimeException("Shopping store service is not responding, please try again later");
    }
}
