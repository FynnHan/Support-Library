package com.libs.brut.androlib.res.data.value;

import com.libs.android.util.TypedValue;
import com.libs.brut.androlib.AndrolibException;

public class ResFractionValue extends ResIntValue {
    public ResFractionValue(int value, String rawValue) {
        super(value, rawValue, "fraction");
    }

    @Override
    protected String encodeAsResXml() throws AndrolibException {
        return TypedValue.coerceToString(TypedValue.TYPE_FRACTION, mValue);
    }
}
