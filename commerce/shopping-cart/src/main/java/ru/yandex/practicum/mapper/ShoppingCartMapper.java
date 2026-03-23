package ru.yandex.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.model.ShoppingCart;

import java.util.Map;
import java.util.UUID;

@UtilityClass
public class ShoppingCartMapper {
    public static ShoppingCartDto mapToDto(ShoppingCart shoppingCart) {
        return new ShoppingCartDto(shoppingCart.getCartId(), shoppingCart.getProducts());
    }

    public static ShoppingCart mapToEntity(ShoppingCartDto shoppingCartDto, String username,
                                           Boolean activated) {
        return new ShoppingCart(shoppingCartDto.cartId(), username, activated, shoppingCartDto.products());
    }

}
