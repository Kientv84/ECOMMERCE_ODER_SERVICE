package com.ecommerce.kientv84.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> { //AuditorAware là interface của spring Data jpa để cung cấp "auditor" của người thực hiện
    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); // Lấy thông tin đặt nhập từ SecurityContextHolder

        // Nếu chưa login hoặc không có user
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.of("SYSTEM"); // fallback user
        }

        // Trả về username đang đăng nhập (Spring Security sẽ tự có)
        return Optional.of(authentication.getName());
    }
}
