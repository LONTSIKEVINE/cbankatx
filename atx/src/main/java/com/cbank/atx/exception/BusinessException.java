package com.cbank.atx.exception;

// Exception métier personnalisée
// → utilisée dans les Services
// → retourne toujours 400
public class BusinessException
        extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}