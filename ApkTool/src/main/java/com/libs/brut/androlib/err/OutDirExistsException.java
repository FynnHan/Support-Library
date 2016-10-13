package com.libs.brut.androlib.err;

import com.libs.brut.androlib.AndrolibException;

public class OutDirExistsException extends AndrolibException {

    public OutDirExistsException(Throwable cause) {
        super(cause);
    }

    public OutDirExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public OutDirExistsException(String message) {
        super(message);
    }

    public OutDirExistsException() {
    }
}
