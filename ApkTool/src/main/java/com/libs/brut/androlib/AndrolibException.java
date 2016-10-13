package com.libs.brut.androlib;

import J.common.BrutException;

public class AndrolibException extends BrutException {
    public AndrolibException() {
    }

    public AndrolibException(String message) {
        super(message);
    }

    public AndrolibException(String message, Throwable cause) {
        super(message, cause);
    }

    public AndrolibException(Throwable cause) {
        super(cause);
    }
}
