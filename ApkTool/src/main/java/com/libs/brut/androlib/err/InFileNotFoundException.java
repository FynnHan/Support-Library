package com.libs.brut.androlib.err;

import com.libs.brut.androlib.AndrolibException;

public class InFileNotFoundException extends AndrolibException {

    public InFileNotFoundException(Throwable cause) {
        super(cause);
    }

    public InFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public InFileNotFoundException(String message) {
        super(message);
    }

    public InFileNotFoundException() {
    }
}
