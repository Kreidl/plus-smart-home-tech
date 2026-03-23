package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.exception.model.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.model.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.model.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.WarehouseProductMapper;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.repository.WarehouseRepository;
import ru.yandex.practicum.warehouse.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.warehouse.dto.AddressDto;
import ru.yandex.practicum.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.warehouse.dto.NewProductInWarehouseRequest;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final AddressDto warehouseAddress = initAddress();

    @Override
    public void addNewProduct(NewProductInWarehouseRequest request) {
        log.info("Starting to add new product");
        if(warehouseRepository.existsById(request.productId())) {
            log.warn("Product with id {} already exists", request.productId());
            throw new SpecifiedProductAlreadyInWarehouseException("Product with id " + request.productId() +
                    " already exists");
        }
        WarehouseProduct warehouseProduct = warehouseRepository.save(WarehouseProductMapper.mapToEntity(request));
        log.debug("New product added {}", warehouseProduct);
    }

    @Override
    public BookedProductsDto checkCart(ShoppingCartDto shoppingCartDto) {
        log.info("Starting to check cart");
        double totalVolume = 0.0;
        double totalWeight = 0.0;
        boolean hasFragile = false;
        Map<UUID, Long> productsInCart = shoppingCartDto.products();
        for(UUID productId : productsInCart.keySet()) {
            WarehouseProduct warehouseProduct = getProductFromWarehouseById(productId);
            log.debug("Product from warehouse found {}", warehouseProduct);
            Long needQuantity = productsInCart.get(productId);
            if(warehouseProduct.getQuantity() < needQuantity) {
                log.warn("Not enough products in warehouse");
                throw new ProductInShoppingCartLowQuantityInWarehouse("Not enough products in warehouse");
            }
            Double productVolume = warehouseProduct.getDepth() * warehouseProduct.getHeight()
                    * warehouseProduct.getWidth();
            totalWeight += warehouseProduct.getWeight() * needQuantity;
            totalVolume += productVolume * needQuantity;
            if(warehouseProduct.getFragile() && !hasFragile) {
                hasFragile = true;
            }
        }
        log.debug("Cart checked: totalWeight={}, totalVolume={}, hasFragile={}", totalWeight, totalVolume, hasFragile);
        return new BookedProductsDto(totalWeight, totalVolume, hasFragile);
    }

    @Override
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        log.info("Starting to add product to warehouse");
        WarehouseProduct warehouseProduct = getProductFromWarehouseById(request.productId());
        log.debug("Product from warehouse found {}", warehouseProduct);
        Long newQuantity = warehouseProduct.getQuantity() + request.quantity();
        warehouseProduct.setQuantity(newQuantity);
        warehouseRepository.save(warehouseProduct);
        log.debug("New product to warehouse added {}", warehouseProduct);
    }

    @Override
    public AddressDto getWarehouseAddress() {
        log.info("Starting to get warehouse address");
        return warehouseAddress;
    }

    private AddressDto initAddress() {
        final String[] addresses = new String[]{"ADDRESS_1", "ADDRESS_2"};
        final String address = addresses[Random.from(new SecureRandom()).nextInt(0, 1)];
        return new AddressDto(address, address, address, address, address);
    }

    private WarehouseProduct getProductFromWarehouseById(UUID productId) {
        return warehouseRepository.findById(productId)
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("Product with id " + productId +
                        " not found on warehouse"));
    }
}
