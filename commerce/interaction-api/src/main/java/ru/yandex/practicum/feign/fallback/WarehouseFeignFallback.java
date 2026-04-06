package ru.yandex.practicum.feign.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.exception.model.FallbackException;
import ru.yandex.practicum.feign.WarehouseFeign;
import ru.yandex.practicum.warehouse.dto.*;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class WarehouseFeignFallback implements WarehouseFeign {
    @Override
    public void addNewProduct(NewProductInWarehouseRequest request) {
        fallback();
    }

    @Override
    public BookedProductsDto checkCart(ShoppingCartDto shoppingCartDto) {
        fallback();
        return null;
    }

    @Override
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        fallback();
    }

    @Override
    public AddressDto getWarehouseAddress() {
        fallback();
        return null;
    }

    @Override
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        fallback();
    }

    @Override
    public BookedProductsDto assemblyProductForOrderFromShoppingCart(AssemblyProductsForOrderRequest request) {
        fallback();
        return null;
    }

    @Override
    public void returnProducts(Map<UUID, Long> products) {
        fallback();
    }

    private void fallback() {
        log.error("Fallback: warehouse service is not responding");
        throw new FallbackException("Warehouse service is not responding, please try again later");
    }
}
