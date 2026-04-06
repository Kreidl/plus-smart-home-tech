package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.payment.enums.PaymentState;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payment_id", unique = true)
    UUID paymentId;

    @Column(name = "order_id")
    UUID orderId;

    @Column(name = "total_payment")
    BigDecimal totalPayment;

    @Column(name = "delivery_total")
    BigDecimal deliveryTotal;

    @Column(name = "fee_total")
    BigDecimal feeTotal;

    @Column(name = "product_total")
    BigDecimal productTotal;

    @Column(name = "payment_state")
    @Enumerated(EnumType.STRING)
    PaymentState paymentState;
}
