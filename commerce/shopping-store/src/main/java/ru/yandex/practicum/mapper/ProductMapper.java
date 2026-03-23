package ru.yandex.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.store.dto.ProductDto;

@UtilityClass
public class ProductMapper {
    public static ProductDto mapToDto(Product product) {
        return new ProductDto(product.getProductId(), product.getProductName(), product.getDescription(),
                product.getImageSrc(), product.getQuantityState(), product.getProductState(),
                product.getProductCategory(), product.getPrice());
    }

    public static Product mapToEntity(ProductDto productDto) {
        return new Product(productDto.productId(), productDto.productName(), productDto.description(),
                productDto.imageSrc(), productDto.quantityState(), productDto.productState(),
                productDto.productCategory(), productDto.price());
    }
}
