package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.cart.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.exception.model.CartActivationException;
import ru.yandex.practicum.exception.model.NoProductsInShoppingCartException;
import ru.yandex.practicum.exception.model.UserAuthorizationException;
import ru.yandex.practicum.mapper.ShoppingCartMapper;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.repository.ShoppingCartRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;

    @Transactional(readOnly = true)
    @Override
    public ShoppingCartDto getCartByUsername(String username) {
        checkUserAuthorization(username);
        return ShoppingCartMapper.mapToDto(getOrCreateNewShoppingCart(username));
    }

    @Override
    public ShoppingCartDto addProductsToCart(String username, Map<UUID, Long> newProducts) {
        checkUserAuthorization(username);
        ShoppingCart shoppingCart = getOrCreateNewShoppingCart(username);
        checkCartActivation(shoppingCart);
        for (Map.Entry<UUID, Long> entry : newProducts.entrySet()) {
            UUID productId = entry.getKey();
            Long quantity = entry.getValue();
            if (shoppingCart.getProducts().containsKey(productId)) {
                changeProductQuantity(username, new ChangeProductQuantityRequest(productId, quantity));
            } else {
                shoppingCart.getProducts().put(productId, quantity);
            }
        }
        return ShoppingCartMapper.mapToDto(shoppingCartRepository.save(shoppingCart));
    }

    @Override
    public void deactivateCart(String username) {
        checkUserAuthorization(username);
        ShoppingCart shoppingCart = getOrCreateNewShoppingCart(username);
        if (!shoppingCart.getActivated()) {
            throw new CartActivationException("The cart is already deactivated");
        }
        shoppingCart.setActivated(false);
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public ShoppingCartDto removeProductsFromCart(String username, List<UUID> products) {
        checkUserAuthorization(username);
        ShoppingCart shoppingCart = getOrCreateNewShoppingCart(username);
        if (shoppingCart == null) {
            throw new NoProductsInShoppingCartException("Cart is empty");
        }
        if (products.isEmpty()) {
            throw new NoProductsInShoppingCartException("Product list for removing is empty");
        }
        checkCartActivation(shoppingCart);
        for (UUID productId : products) {
            if (!shoppingCart.getProducts().containsKey(productId)) {
                throw new NoProductsInShoppingCartException("Product with id " + productId + " not found in cart");
            }
            shoppingCart.getProducts().remove(productId);
        }
        return ShoppingCartMapper.mapToDto(shoppingCartRepository.save(shoppingCart));
    }

    @Override
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        checkUserAuthorization(username);
        ShoppingCart shoppingCart = getOrCreateNewShoppingCart(username);
        if (shoppingCart == null) {
            throw new NoProductsInShoppingCartException("Cart is empty");
        }
        checkCartActivation(shoppingCart);
        if (!shoppingCart.getProducts().containsKey(request.productId())) {
            throw new NoProductsInShoppingCartException("Product with id " + request.productId()
                    + " not found in cart");
        }
        shoppingCart.getProducts().put(request.productId(), request.quantity());
        return ShoppingCartMapper.mapToDto(shoppingCartRepository.save(shoppingCart));
    }

    private ShoppingCart getOrCreateNewShoppingCart(String username) {
        return shoppingCartRepository.findByUsername(username)
                .orElseGet(() ->
                        shoppingCartRepository.save(createCart(username, new HashMap<>())));
    }

    private ShoppingCart createCart(String username, Map<UUID, Long> products) {
        ShoppingCart newCart = new ShoppingCart();
        newCart.setUsername(username);
        newCart.setActivated(true);
        newCart.setProducts(products);
        return newCart;
    }

    private void checkUserAuthorization(String username) {
        if (username.isBlank()) {
            throw new UserAuthorizationException("Username cannot be blank");
        }
    }

    private void checkCartActivation(ShoppingCart shoppingCart) {
        if (!shoppingCart.getActivated()) {
            throw new CartActivationException("The cart is deactivated");
        }
    }
}
