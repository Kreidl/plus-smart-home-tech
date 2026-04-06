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
import ru.yandex.practicum.model.OrderBooking;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.repository.OrderBookingRepository;
import ru.yandex.practicum.repository.WarehouseRepository;
import ru.yandex.practicum.warehouse.dto.*;

import java.security.SecureRandom;
import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final OrderBookingRepository orderBookingRepository;
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
        Map<UUID, Long> productsInCart = shoppingCartDto.products();
        BookedProductsDto bookedProductsDto = checkProductQuantityInWarehouse(productsInCart);
        log.debug("Cart checked: totalWeight={}, totalVolume={}, hasFragile={}", bookedProductsDto.deliveryWeight(),
                bookedProductsDto.deliveryVolume(), bookedProductsDto.fragile());
        return bookedProductsDto;
    }

    @Override
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        log.info("Starting to add product count to warehouse");
        WarehouseProduct warehouseProduct = getProductFromWarehouseById(request.productId());
        log.debug("Product from warehouse found {}", warehouseProduct);
        Long newQuantity = warehouseProduct.getQuantity() + request.quantity();
        warehouseProduct.setQuantity(newQuantity);
        warehouseRepository.save(warehouseProduct);
        log.debug("Product count to warehouse added {}", warehouseProduct);
    }

    @Override
    public AddressDto getWarehouseAddress() {
        log.info("Starting to get warehouse address");
        return warehouseAddress;
    }

    @Override
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        log.info("Starting to set delivery id in bookings");
        List<OrderBooking> orderBookings = orderBookingRepository.findByOrderId(request.orderId());
        for (OrderBooking orderBooking : orderBookings) {
            orderBooking.setDeliveryId(request.deliveryId());
        }
        orderBookingRepository.saveAll(orderBookings);
        log.debug("Delivery id in bookings updated, {}", orderBookings);
    }

    @Override
    public BookedProductsDto assemblyProductForOrderFromShoppingCart(AssemblyProductsForOrderRequest request) {
        log.info("Start of product reduction from warehouse");
        Map<UUID, Long> products = request.products();
        BookedProductsDto bookedProductsDto = checkProductQuantityInWarehouse(products);
        List<WarehouseProduct> productList = warehouseRepository.findAllById(products.keySet());
        List<OrderBooking> orderBookings = new ArrayList<>();
        for (WarehouseProduct product : productList) {
            product.setQuantity(product.getQuantity() - products.get(product.getProductId()));
            OrderBooking orderBooking = new OrderBooking();
            orderBooking.setOrderId(request.orderId());
            orderBooking.setProductId(product.getProductId());
            orderBooking.setQuantity(product.getQuantity());
            orderBookings.add(orderBooking);
        }
        warehouseRepository.saveAll(productList);
        orderBookingRepository.saveAll(orderBookings);
        log.debug("Product reduction from warehouse complete {}", productList);
        return bookedProductsDto;
    }

    public void returnProducts(Map<UUID, Long> products) {
        log.info("Start returning products in warehouse");
        List<WarehouseProduct> productList = warehouseRepository.findAllById(products.keySet());
        for (WarehouseProduct product : productList) {
            product.setQuantity(product.getQuantity() + products.get(product.getProductId()));
        }
        warehouseRepository.saveAll(productList);
        log.info("Products in warehouse returned {}", productList);
    }

    private BookedProductsDto checkProductQuantityInWarehouse(Map<UUID, Long> products) {
        log.info("Check products quantity in warehouse {}", products);
        double totalVolume = 0.0;
        double totalWeight = 0.0;
        boolean hasFragile = false;
        for(UUID productId : products.keySet()) {
            WarehouseProduct warehouseProduct = getProductFromWarehouseById(productId);
            log.debug("Product from warehouse found {}", warehouseProduct);
            Long needQuantity = products.get(productId);
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
        log.info("Products quantity in warehouse checked");
        return new BookedProductsDto(totalWeight, totalVolume, hasFragile);
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
