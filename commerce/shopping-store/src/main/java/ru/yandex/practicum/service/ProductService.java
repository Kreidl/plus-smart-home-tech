package ru.yandex.practicum.service;

import org.springframework.data.domain.Page;
import ru.yandex.practicum.store.dto.ProductDto;
import ru.yandex.practicum.store.dto.SetProductQuantityState;
import ru.yandex.practicum.store.enums.ProductCategory;

import java.util.UUID;

public interface ProductService {
    Page<ProductDto> getProductsByCategory(ProductCategory productCategory, int page, int size, String sort);

    ProductDto getProductById(UUID productId);

    ProductDto createNewProduct(ProductDto productDto);

    ProductDto updateProduct(ProductDto productDto);

    Boolean removeProductById(UUID productId);

    Boolean setProductQuantityState(SetProductQuantityState request);
}
