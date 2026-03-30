package ru.yandex.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.warehouse.dto.NewProductInWarehouseRequest;

@UtilityClass
public class WarehouseProductMapper {
    public static WarehouseProduct mapToEntity(NewProductInWarehouseRequest request) {
        WarehouseProduct warehouseProduct = new WarehouseProduct();
        warehouseProduct.setProductId(request.productId());
        warehouseProduct.setWidth(request.dimension().width());
        warehouseProduct.setHeight(request.dimension().height());
        warehouseProduct.setDepth(request.dimension().depth());
        warehouseProduct.setWeight(request.weight());
        warehouseProduct.setFragile(request.fragile());
        warehouseProduct.setQuantity(0L);
        return warehouseProduct;
    }
}
