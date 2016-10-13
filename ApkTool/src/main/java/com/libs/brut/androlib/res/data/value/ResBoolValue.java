package com.libs.brut.androlib.res.data.value;

public class ResBoolValue extends ResScalarValue {
    private final boolean mValue;

    public ResBoolValue(boolean value, int rawIntValue, String rawValue) {
        super("bool", rawIntValue, rawValue);
        this.mValue = value;
    }

    public boolean getValue() {
        return mValue;
    }

    @Override
    protected String encodeAsResXml() {
        return mValue ? "true" : "false";
    }
}
