package com.ecommerce.kientv84.utils;


import java.util.function.Consumer;
import java.util.function.Supplier;

public class FieldUpdaterUtil {

    /**
     * Hàm này kiểm tra nếu giá trị mới khác giá trị hiện tại thì set lại, ngược lại ghi nhận là không thay đổi.
     *
     * @param currentGetter   Getter lấy giá trị hiện tại
     * @param newGetter       Getter lấy giá trị mới
     * @param setter          Setter cập nhật giá trị
     * @param unchangedFields StringBuilder lưu danh sách các field không thay đổi
     * @param fieldName       Tên field (để ghi log hoặc báo người dùng)
     * @param <T>             Kiểu dữ liệu generic
     * @return true nếu có thay đổi và cập nhật
     */
    public static <T> boolean updateIfChanged(
            Supplier<T> currentGetter,
            Supplier<T> newGetter,
            Consumer<T> setter,
            StringBuilder unchangedFields,
            String fieldName)
    {
        T currentValue = currentGetter.get();
        T newValue = newGetter.get();

        if (newValue != null && !newValue.equals(currentValue)) {
            setter.accept(newValue);
            return true;
        } else if (newValue != null) {
            unchangedFields.append(fieldName).append(", ");
        }

        return false;
    }
}