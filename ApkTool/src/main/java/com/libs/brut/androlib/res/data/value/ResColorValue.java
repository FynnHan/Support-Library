package com.libs.brut.androlib.res.data.value;

public class ResColorValue extends ResIntValue {
    public ResColorValue(int value, String rawValue) {
        super(value, rawValue, "color");
    }

    @Override
    protected String encodeAsResXml() {
        return String.format("#%08x", mValue);
    }
}