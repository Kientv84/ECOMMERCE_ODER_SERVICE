package com.ecommerce.kientv84.enums;

public enum PaymentStatus {
    PENDING,
    COD_PENDING,       // Đang chờ thanh toán (ví dụ COD chưa giao)
    PROCESSING,    // Đang xác thực bên thứ 3 (Momo, VNPay...)
    PAID,       // Thanh toán thành công
    FAILED,        // Thanh toán thất bại
    CANCELLED ,     //  // Thanh toán thất bại
    REFUNDED    // Đã hoàn tiền (nếu có)
}
