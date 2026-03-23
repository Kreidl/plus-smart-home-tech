package ru.yandex.practicum.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.cart.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name= "shopping-cart", path = "/api/v1/shopping-cart")
public interface ShoppingCartFeign {
    @GetMapping
    ShoppingCartDto getCartByUsername(@RequestParam(name = "username") String username);

    @PutMapping
    ShoppingCartDto addProductInCart(@RequestParam(name = "username") String username,
                                      @RequestBody Map<UUID, Long> products);

    @DeleteMapping
    void deactivateCart(@RequestParam(name = "username") String username);

    @PostMapping
    ShoppingCartDto removeProductsFromCart(@RequestParam(name = "username") String username,
                                           @RequestBody List<UUID> products);

    @PostMapping
    ShoppingCartDto changeProductQuantity(@RequestParam(name = "username") String username,
                                          @RequestBody ChangeProductQuantityRequest request);

}
