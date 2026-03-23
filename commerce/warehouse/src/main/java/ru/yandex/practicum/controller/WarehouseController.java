package ru.yandex.practicum.controller;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.feign.WarehouseFeign;
import ru.yandex.practicum.service.WarehouseService;
import ru.yandex.practicum.warehouse.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.warehouse.dto.AddressDto;
import ru.yandex.practicum.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.warehouse.dto.NewProductInWarehouseRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/warehouse")
public class WarehouseController implements WarehouseFeign {
    private final WarehouseService warehouseService;

    @Override
    @ResponseStatus(HttpStatus.OK)
    public void addNewProduct(NewProductInWarehouseRequest request) throws FeignException {
        warehouseService.addNewProduct(request);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public BookedProductsDto checkCart(ShoppingCartDto shoppingCartDto) throws FeignException {
        return warehouseService.checkCart(shoppingCartDto);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public void addProductToWarehouse(AddProductToWarehouseRequest request) throws FeignException {
        warehouseService.addProductToWarehouse(request);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public AddressDto getWarehouseAddress() throws FeignException {
        return warehouseService.getWarehouseAddress();
    }
}
