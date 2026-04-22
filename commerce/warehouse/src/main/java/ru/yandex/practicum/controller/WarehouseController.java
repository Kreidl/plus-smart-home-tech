package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.feign.WarehouseFeign;
import ru.yandex.practicum.service.WarehouseService;
import ru.yandex.practicum.warehouse.dto.*;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/warehouse")
public class WarehouseController implements WarehouseFeign {
    private final WarehouseService warehouseService;

    @Override
    @ResponseStatus(HttpStatus.OK)
    public void addNewProduct(NewProductInWarehouseRequest request) {
        log.info("New request to add new product {}", request);
        warehouseService.addNewProduct(request);
        log.debug("New product added");
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public BookedProductsDto checkCart(ShoppingCartDto shoppingCartDto) {
        log.info("New request to check cart {}", shoppingCartDto);
        return warehouseService.checkCart(shoppingCartDto);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        log.info("New request to add product to warehouse {}", request);
        warehouseService.addProductToWarehouse(request);
        log.debug("Product added");
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public AddressDto getWarehouseAddress() {
        log.info("New request to get warehouse address");
        return warehouseService.getWarehouseAddress();
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        log.info("New request to set delivery id in bookings");
        warehouseService.shippedToDelivery(request);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public BookedProductsDto assemblyProductForOrderFromShoppingCart(AssemblyProductsForOrderRequest request) {
        log.info("New request of product reduction from warehouse");
        return warehouseService.assemblyProductForOrderFromShoppingCart(request);
    }

    @Override
    public void returnProducts(Map<UUID, Long> products) {
        log.info("New request to return products in warehouse");
        warehouseService.returnProducts(products);
    }
}
