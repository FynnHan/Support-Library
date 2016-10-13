package com.libs.brut.androlib.err;

import com.libs.brut.androlib.AndrolibException;

public class UndefinedResObject extends AndrolibException {
    public UndefinedResObject(Throwable cause) {
        super(cause);
    }

    public UndefinedResObject(String message, Throwable cause) {
        super(message, cause);
    }

    public UndefinedResObject(String message) {
        super(message);
    }

    public UndefinedResObject() {
    }
}
