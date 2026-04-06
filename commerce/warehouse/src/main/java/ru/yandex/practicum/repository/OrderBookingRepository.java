package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.OrderBooking;

import java.util.List;
import java.util.UUID;

public interface OrderBookingRepository extends JpaRepository<OrderBooking, UUID> {
    List<OrderBooking> findByOrderId(UUID orderId);

    void deleteByOrderId(UUID orderId);
}
