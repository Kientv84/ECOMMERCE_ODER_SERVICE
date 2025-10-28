package com.ecommerce.kientv84.entities;

import com.ecommerce.kientv84.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "order_entity")
@EntityListeners(AuditingEntityListener.class)
public class OrderEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    private UUID userId; // Nếu user tách service, chỉ lưu userId

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemEntity> orderItems;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    // Payment
    @Column(name = "payment_id", columnDefinition = "uuid")
    private UUID paymentId;

    // Shipping
    @Column(name = "shipping_address", length = 500)
    private String shippingAddress;

    // ====== Metadata ======
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name ="create_date")
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    @Column(name ="update_date")
    private Date updatedDate;

    @CreatedBy
    @Column(name ="created_by")
    private String createdBy;

    @LastModifiedBy
    @Column(name ="updated_by")
    private String updatedBy;
}
