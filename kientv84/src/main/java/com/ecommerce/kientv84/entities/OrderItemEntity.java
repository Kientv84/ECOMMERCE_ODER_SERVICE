package com.ecommerce.kientv84.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.context.event.EventListener;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "order_item_entity")
@EntityListeners(AuditingEntityListener.class)
public class OrderItemEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, columnDefinition = "uuid")
    private OrderEntity order;

    @Column(name = "product_id", nullable = false, columnDefinition = "uuid")
    private UUID productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_price", nullable = false)
    private BigDecimal productPrice;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    // (product_price * quantity)
    @Column(name = "line_total", nullable = false)
    private BigDecimal lineTotal;

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