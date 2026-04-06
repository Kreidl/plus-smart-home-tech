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
@Table(name = "warehouse_booking_products")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "booking_id")
    UUID bookingId;

    @Column(name = "order_id")
    UUID orderId;

    @Column(name = "product_id")
    UUID productId;

    @Column(name = "booked_quantity")
    Long quantity;

    @Column(name = "delivery_id")
    UUID deliveryId;
}
