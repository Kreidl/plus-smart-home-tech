package ru.yandex.practicum.controller;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.feign.ShoppingStoreFeign;
import ru.yandex.practicum.service.ProductService;
import ru.yandex.practicum.store.dto.ProductDto;
import ru.yandex.practicum.store.dto.SetProductQuantityState;
import ru.yandex.practicum.store.enums.ProductCategory;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-store")
public class ProductController implements ShoppingStoreFeign {
    private final ProductService productService;

    @Override
    @ResponseStatus(HttpStatus.OK)
    public Page<ProductDto> getProductsByCategory(ProductCategory productCategory, int page, int size, String sort) {
        return productService.getProductsByCategory(productCategory, page, size, sort);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ProductDto getProductById(UUID productId) {
        return productService.getProductById(productId);
    }

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto createNewProduct(ProductDto productDto) {
        return productService.createNewProduct(productDto);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ProductDto updateProduct(ProductDto productDto) {
        return productService.updateProduct(productDto);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public Boolean removeProductById(UUID productId) {
        return productService.removeProductById(productId);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public Boolean setProductQuantityState(SetProductQuantityState request) {
        return productService.setProductQuantityState(request);
    }
}
