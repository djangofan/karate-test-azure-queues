package com.test.functions.exception;

import lombok.Getter;

@Getter
public class DocumentException extends RuntimeException {
    private int code;
    public DocumentException(final String message) {
        super(message);
    }

    public DocumentException(String message, Exception e) {
        super(message, e);
    }

    public DocumentException(int code, String message) {
        super(message);
        this.code = code;
    }

    public DocumentException() {
        super();
    }
}
