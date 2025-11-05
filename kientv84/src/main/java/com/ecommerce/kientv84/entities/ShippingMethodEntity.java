package com.ecommerce.kientv84.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "shipping_methods_entity")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ShippingMethodEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String shippingCode;        // GHN, GHTK, INTERNAL, PICKUP
    private String shippingName;        // "Giao Hàng Nhanh", "Giao Hàng Tiết Kiệm", "Tự giao"
    private BigDecimal baseFee; // Phí cơ bản
    private String description; // Mô tả thêm
    private Boolean status;     // Có đang được bật hay không
    @Column(name ="thumbnail_url")
    private String thumbnailUrl; // Ảnh chính

    @OneToMany(mappedBy = "shippingMethod", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderEntity> orders;

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
