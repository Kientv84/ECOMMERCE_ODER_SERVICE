package com.ecommerce.kientv84.exceptions;


import com.ecommerce.kientv84.exceptions.ServiceException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum EnumError {

    //----------- ORDER ------------
    ORDER_DATA_EXISTED("ORDER-DTE", "Data exit", HttpStatus.CONFLICT),

    ORDER_GET_ERROR("ORDER-GET-ERROR", "Have error in process get order", HttpStatus.BAD_REQUEST),
    INTERNAL_ERROR("ACC-S-999", "Unexpected internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;


    public static EnumError fromCode(String code) {
        for (EnumError e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unknown DispatchError code: " + code);
    }
}

