package com.libs.brut.androlib.res.data.value;

import com.libs.brut.androlib.AndrolibException;

public class ResEmptyValue extends ResScalarValue {
    protected final int mValue;
    protected int type;

    public ResEmptyValue(int value, String rawValue, int type) {
        this(value, rawValue, "integer");
        this.type = type;
    }

    public ResEmptyValue(int value, String rawValue, String type) {
        super(type, value, rawValue);
        if (value != 1)
            throw new UnsupportedOperationException();
        this.mValue = value;
    }

    public int getValue() {
        return mValue;
    }

    @Override
    protected String encodeAsResXml() throws AndrolibException {
        return "@empty";
    }
}