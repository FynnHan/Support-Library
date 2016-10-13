package com.libs.brut.androlib.res.data.value;

import com.libs.android.util.TypedValue;
import com.libs.brut.androlib.AndrolibException;

public class ResIntValue extends ResScalarValue {
    protected final int mValue;
    private int type;

    public ResIntValue(int value, String rawValue, int type) {
        this(value, rawValue, "integer");
        this.type = type;
    }

    public ResIntValue(int value, String rawValue, String type) {
        super(type, value, rawValue);
        this.mValue = value;
    }

    public int getValue() {
        return mValue;
    }

    @Override
    protected String encodeAsResXml() throws AndrolibException {
        return TypedValue.coerceToString(type, mValue);
    }
}