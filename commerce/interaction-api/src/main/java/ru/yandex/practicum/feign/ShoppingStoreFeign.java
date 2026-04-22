package ru.yandex.practicum.feign;

import feign.FeignException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.feign.fallback.ShoppingStoreFeignFallback;
import ru.yandex.practicum.store.dto.ProductDto;
import ru.yandex.practicum.store.dto.SetProductQuantityState;
import ru.yandex.practicum.store.enums.ProductCategory;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "shopping-store", path = "/api/v1/shopping-store",
        fallback = ShoppingStoreFeignFallback.class)
public interface ShoppingStoreFeign {
    @GetMapping
    Page<ProductDto> getProductsByCategory(@RequestParam(name = "category") ProductCategory productCategory,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "20") int size,
                                           @RequestParam(required = false) String sort) throws FeignException;

    @GetMapping("/{productId}")
    ProductDto getProductById(@PathVariable UUID productId) throws FeignException;

    @PutMapping
    ProductDto createNewProduct(@Valid @RequestBody ProductDto productDto) throws FeignException;

    @PostMapping
    ProductDto updateProduct(@Valid @RequestBody ProductDto productDto) throws FeignException;

    @PostMapping("/removeProductFromStore")
    Boolean removeProductById(@RequestBody UUID productId) throws FeignException;

    @PostMapping("/quantityState")
    Boolean setProductQuantityState(@Valid SetProductQuantityState request) throws FeignException;

    @GetMapping("/ids")
    List<ProductDto> getProductsById(@NotNull @NotEmpty @RequestBody List<UUID> productIds) throws FeignException;
}
