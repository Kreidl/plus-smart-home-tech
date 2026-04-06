package ru.yandex.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.order.dto.CreateNewOrderRequest;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.enums.OrderState;
import ru.yandex.practicum.warehouse.dto.BookedProductsDto;

@UtilityClass
public class OrderMapper {
    public static Order mapToEntity(CreateNewOrderRequest newOrder, BookedProductsDto bookedProductsDto) {
        return  new Order(null, OrderState.NEW, newOrder.shoppingCartDto().products(),
                newOrder.shoppingCartDto().cartId(), null, null, bookedProductsDto.deliveryWeight(),
                bookedProductsDto.deliveryVolume(), bookedProductsDto.fragile(), null,
                null, null);
    }

    public static OrderDto mapToDto(Order order) {
        return new OrderDto(order.getOrderId(), order.getShoppingCartId(), order.getProducts(), order.getPaymentId(),
                order.getDeliveryId(), order.getState(), order.getDeliveryWeight(), order.getDeliveryVolume(),
                order.isFragile(), order.getTotalPrice(), order.getDeliveryPrice(), order.getProductPrice());
    }
}
