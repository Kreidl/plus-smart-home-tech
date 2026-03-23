package ru.yandex.practicum.controller;

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
        log.info("New request to get products by category {} with page parameters: page={}, size={}, sort={}",
                productCategory, page, size, sort);
        return productService.getProductsByCategory(productCategory, page, size, sort);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ProductDto getProductById(UUID productId) {
        log.info("New request to get product by id {}",productId);
        return productService.getProductById(productId);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ProductDto createNewProduct(ProductDto productDto) {
        log.info("New request to create new product {}",productDto);
        return productService.createNewProduct(productDto);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ProductDto updateProduct(ProductDto productDto) {
        log.info("New request to update product {}",productDto);
        return productService.updateProduct(productDto);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public Boolean removeProductById(UUID productId) {
        log.info("New request to remove product with id {}",productId);
        return productService.removeProductById(productId);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public Boolean setProductQuantityState(SetProductQuantityState request) {
        log.info("New request to set quantity state {}", request);
        return productService.setProductQuantityState(request);
    }
}
