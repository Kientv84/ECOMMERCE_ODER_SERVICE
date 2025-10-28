package com.ecommerce.kientv84.exceptions;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ServiceException extends RuntimeException {

    private final EnumError enumError;
    private final String errorCode;
    private final String messageCode;
    private final HttpStatus httpStatus;
    private final Object[] params;

    public ServiceException(EnumError error) {
        this(error, error.name().toLowerCase(), null);
    }

    public ServiceException(EnumError error, String messageCode) {
        this(error, messageCode, null);
    }

    public ServiceException(EnumError error, String messageCode, Object[] params) {
        super(error.getDefaultMessage());
        this.enumError = error;
        this.errorCode = error.getCode();
        this.messageCode = messageCode;
        this.httpStatus = error.getHttpStatus();
        this.params = params;
    }

    public Object[] getParams() {
        return params;
    }
}
