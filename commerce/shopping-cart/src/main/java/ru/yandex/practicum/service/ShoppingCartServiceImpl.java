package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.cart.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.exception.model.CartActivationException;
import ru.yandex.practicum.exception.model.NoProductsInShoppingCartException;
import ru.yandex.practicum.exception.model.UserAuthorizationException;
import ru.yandex.practicum.feign.WarehouseFeign;
import ru.yandex.practicum.mapper.ShoppingCartMapper;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.repository.ShoppingCartRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final WarehouseFeign warehouseFeign;

    @Transactional(readOnly = true)
    @Override
    public ShoppingCartDto getCartByUsername(String username) {
        log.info("Start getting cart by username");
        checkUserAuthorization(username);
        return ShoppingCartMapper.mapToDto(getOrCreateNewShoppingCart(username));
    }

    @Override
    public ShoppingCartDto addProductsToCart(String username, Map<UUID, Long> newProducts) {
        log.info("Start adding products to cart");
        checkUserAuthorization(username);
        ShoppingCart shoppingCart = getOrCreateNewShoppingCart(username);
        checkCartActivation(shoppingCart);
        log.info("Start updating products in cart");
        for (Map.Entry<UUID, Long> entry : newProducts.entrySet()) {
            UUID productId = entry.getKey();
            Long quantity = entry.getValue();
            if (shoppingCart.getProducts().containsKey(productId)) {
                changeProductQuantity(username, new ChangeProductQuantityRequest(productId, quantity));
            } else {
                shoppingCart.getProducts().put(productId, quantity);
            }
        }
        warehouseFeign.checkCart(ShoppingCartMapper.mapToDto(shoppingCart));
        return ShoppingCartMapper.mapToDto(shoppingCartRepository.save(shoppingCart));
    }

    @Override
    public void deactivateCart(String username) {
        log.info("Start deactivating cart");
        checkUserAuthorization(username);
        ShoppingCart shoppingCart = getOrCreateNewShoppingCart(username);
        if (!shoppingCart.getActivated()) {
            log.warn("The cart is already deactivated {}", shoppingCart);
            throw new CartActivationException("The cart is already deactivated");
        }
        shoppingCart.setActivated(false);
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public ShoppingCartDto removeProductsFromCart(String username, List<UUID> products) {
        log.info("Start removing products from cart");
        checkUserAuthorization(username);
        ShoppingCart shoppingCart = getOrCreateNewShoppingCart(username);
        if (shoppingCart == null) {
            log.warn("Cart is empty");
            throw new NoProductsInShoppingCartException("Cart is empty");
        }
        checkCartActivation(shoppingCart);
        for (UUID productId : products) {
            if (!shoppingCart.getProducts().containsKey(productId)) {
                log.warn("Product with id {} not found in cart", productId);
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
            log.warn("Cart is empty");
            throw new NoProductsInShoppingCartException("Cart is empty");
        }
        checkCartActivation(shoppingCart);
        if (!shoppingCart.getProducts().containsKey(request.productId())) {
            log.warn("Product with id {} not found in cart", request.productId());
            throw new NoProductsInShoppingCartException("Product with id " + request.productId()
                    + " not found in cart");
        }
        shoppingCart.getProducts().put(request.productId(), request.newQuantity());
        warehouseFeign.checkCart(ShoppingCartMapper.mapToDto(shoppingCart));
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
        log.debug("New cart for user {} created", username);
        return newCart;
    }

    private void checkUserAuthorization(String username) {
        log.info("Start checking user authorization");
        if (username.isBlank()) {
            log.warn("Username cannot be blank");
            throw new UserAuthorizationException("Username cannot be blank");
        }
    }

    private void checkCartActivation(ShoppingCart shoppingCart) {
        log.info("Start checking cart activation");
        if (!shoppingCart.getActivated()) {
            log.warn("The cart is deactivated {}", shoppingCart);
            throw new CartActivationException("The cart is deactivated");
        }
    }
}
