package ru.yandex.practicum.feign.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.feign.ShoppingStoreFeign;
import ru.yandex.practicum.store.dto.ProductDto;
import ru.yandex.practicum.store.dto.SetProductQuantityState;
import ru.yandex.practicum.store.enums.ProductCategory;

import java.util.UUID;

@Slf4j
@Component
public class ShoppingStoreFeignFallback implements ShoppingStoreFeign {
    @Override
    public Page<ProductDto> getProductsByCategory(ProductCategory productCategory, int page, int size, String sort) {
        fallback();
        return null;
    }

    @Override
    public ProductDto getProductById(UUID productId) {
        fallback();
        return null;
    }

    @Override
    public ProductDto createNewProduct(ProductDto productDto) {
        fallback();
        return null;
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        fallback();
        return null;
    }

    @Override
    public Boolean removeProductById(UUID productId) {
        fallback();
        return null;
    }

    @Override
    public Boolean setProductQuantityState(SetProductQuantityState request) {
        fallback();
        return null;
    }

    private void fallback() {
        log.error("Fallback: shopping-store service is not responding");
        throw new RuntimeException("Shopping store service is not responding, please try again later");
    }
}
