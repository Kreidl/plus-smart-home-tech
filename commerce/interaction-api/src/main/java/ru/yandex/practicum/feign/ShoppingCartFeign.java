package ru.yandex.practicum.feign;

import feign.FeignException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    ShoppingCartDto getCartByUsername(@RequestParam(name = "username") String username) throws FeignException;

    @PutMapping
    ShoppingCartDto addProductInCart(@RequestParam(name = "username") String username,
                                     @NotNull @NotEmpty @RequestBody Map<UUID, Long> products) throws FeignException;

    @DeleteMapping
    void deactivateCart(@RequestParam(name = "username") String username) throws FeignException;

    @PostMapping("/remove")
    ShoppingCartDto removeProductsFromCart(@RequestParam(name = "username") String username,
                                           @RequestBody List<UUID> products) throws FeignException;

    @PostMapping("/change-quantity")
    ShoppingCartDto changeProductQuantity(@RequestParam(name = "username") String username,
                                          @Valid @RequestBody ChangeProductQuantityRequest request)
            throws FeignException;

}
