package com.libs.brut.androlib.res.data.value;

public class ResFloatValue extends ResScalarValue {
    private final float mValue;

    public ResFloatValue(float value, int rawIntValue, String rawValue) {
        super("float", rawIntValue, rawValue);
        this.mValue = value;
    }

    public float getValue() {
        return mValue;
    }

    @Override
    protected String encodeAsResXml() {
        return String.valueOf(mValue);
    }
}
