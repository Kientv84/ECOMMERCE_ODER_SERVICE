package com.ecommerce.kientv84.enums;

public enum PaymentStatus {
    UNPAID,     // Chưa thanh toán
    PENDING,    // Đang chờ xử lý (ví dụ chờ MOMO, VNPAY xác nhận)
    PAID,       // Đã thanh toán
    FAILED,     // Thanh toán thất bại
    REFUNDED    // Đã hoàn tiền (nếu có)
}
