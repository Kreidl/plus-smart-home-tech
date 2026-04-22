package ru.yandex.practicum.service;

import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.warehouse.dto.*;

import java.util.Map;
import java.util.UUID;

public interface WarehouseService {
    void addNewProduct(NewProductInWarehouseRequest request);

    BookedProductsDto checkCart(ShoppingCartDto shoppingCartDto);

    void addProductToWarehouse(AddProductToWarehouseRequest request);

    AddressDto getWarehouseAddress();

    void shippedToDelivery(ShippedToDeliveryRequest request);

    BookedProductsDto assemblyProductForOrderFromShoppingCart(AssemblyProductsForOrderRequest request);

    void returnProducts(Map<UUID, Long> products);
}
