package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
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

@Service
@Transactional
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final AddressDto warehouseAddress = initAddress();

    @Override
    public void addNewProduct(NewProductInWarehouseRequest request) {
        if(warehouseRepository.existsById(request.productId())) {
            throw new SpecifiedProductAlreadyInWarehouseException("Product with id " + request.productId() +
                    " already exists");
        }
        warehouseRepository.save(WarehouseProductMapper.mapToEntity(request));
    }

    @Override
    public BookedProductsDto checkCart(ShoppingCartDto shoppingCartDto) {
        double totalVolume = 0.0;
        double totalWeight = 0.0;
        boolean hasFragile = false;
        Map<UUID, Long> productsInCart = shoppingCartDto.products();
        for(UUID productId : productsInCart.keySet()) {
            WarehouseProduct warehouseProduct = getProductFromWarehouseById(productId);
            Long needQuantity = productsInCart.get(productId);
            if(warehouseProduct.getQuantity() < needQuantity) {
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
        return new BookedProductsDto(totalWeight, totalVolume, hasFragile);
    }

    @Override
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        WarehouseProduct warehouseProduct = getProductFromWarehouseById(request.productId());
        Long newQuantity = warehouseProduct.getQuantity() + request.quantity();
        warehouseProduct.setQuantity(newQuantity);
        warehouseRepository.save(warehouseProduct);
    }

    @Override
    public AddressDto getWarehouseAddress() {
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
