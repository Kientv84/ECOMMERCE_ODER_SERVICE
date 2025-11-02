package com.ecommerce.kientv84.enums;

public enum OrderStatus {
    CREATED,            // Người dùng vừa đặt hàng (đã lưu DB)
    CONFIRMED,          // Hệ thống/nhân viên xác nhận đơn
    PROCESSING,         // Đang xử lý (đóng gói, chuẩn bị giao)
    SHIPPED,            // Đã gửi cho đơn vị vận chuyển
    IN_TRANSIT,         // Đang vận chuyển
    DELIVERED,          // Đã giao hàng thành công
    RETURN_REQUESTED,   // Người dùng yêu cầu trả hàng
    RETURNED,           // Đã trả hàng
    CANCELLED,          // Đơn bị hủy
    FAILED_DELIVERY     // Giao hàng thất bại
}
