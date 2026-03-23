package ru.yandex.practicum.service;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.exception.model.ProductNotFoundException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.ProductRepository;
import ru.yandex.practicum.store.dto.ProductDto;
import ru.yandex.practicum.store.dto.SetProductQuantityState;
import ru.yandex.practicum.store.enums.ProductCategory;
import ru.yandex.practicum.store.enums.ProductState;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> getProductsByCategory(ProductCategory productCategory, int page, int size, String sort) {
        Pageable pageable = createPageable(page, size, sort);
        return productRepository.findByProductCategory(productCategory, pageable)
                .map(ProductMapper::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProductById(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + productId + " not found"));
        return ProductMapper.mapToDto(product);
    }

    @Override
    public ProductDto createNewProduct(ProductDto productDto) {
        return ProductMapper.mapToDto(productRepository.save(ProductMapper.mapToEntity(productDto)));
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        if (productDto.id() == null) {
            throw new IllegalArgumentException("Product id can not be null");
        }
        productRepository.findById(productDto.id())
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + productDto.id() + " not found"));
        return ProductMapper.mapToDto(productRepository.save(ProductMapper.mapToEntity(productDto)));
    }

    @Override
    public Boolean removeProductById(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + productId + " not found"));
        if (product.getProductState().equals(ProductState.DEACTIVATE)) {
            return false;
        }
        product.setProductState(ProductState.DEACTIVATE);
        productRepository.save(product);
        return true;
    }

    @Override
    public Boolean setProductQuantityState(SetProductQuantityState request) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + request.productId() + " not found"));
        if (product.getQuantityState().equals(request.quantityState())) {
            return false;
        }
        product.setQuantityState(request.quantityState());
        productRepository.save(product);
        return true;
    }

    private Pageable createPageable(int page, int size, String sort) {
        if (sort == null || sort.isBlank()) {
            return PageRequest.of(page, size);
        }
        String[] parts = sort.split(",", 2);
        String property = parts[0].trim();
        if (parts.length == 1) {
            return PageRequest.of(page, size, Sort.by(property));
        }
        String directionStr = parts[1].trim();
        Sort.Direction direction = directionStr.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(direction, property));
    }
}
