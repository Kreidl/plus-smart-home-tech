package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "warehouse_products")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarehouseProduct {
    @Id
    @Column(name = "product_id")
    UUID productId;

    @Column(name = "width")
    Double width;

    @Column(name = "height")
    Double height;

    @Column(name = "depth")
    Double depth;

    @Column(name = "weight")
    Double weight;

    @Column(name = "fragile")
    Boolean fragile;

    @Column(name = "quantity")
    Long quantity;
}
