package com.libs.brut.androlib.err;

import com.libs.brut.androlib.AndrolibException;

public class CantFindFrameworkResException extends AndrolibException {

    public CantFindFrameworkResException(Throwable cause, int id) {
        super(cause);
        mPkgId = id;
    }

    public CantFindFrameworkResException(int id) {
        mPkgId = id;
    }

    public int getPkgId() {
        return mPkgId;
    }

    private final int mPkgId;
}
