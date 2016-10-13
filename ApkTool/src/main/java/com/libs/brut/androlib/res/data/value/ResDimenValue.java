package com.libs.brut.androlib.res.data.value;

import com.libs.android.util.TypedValue;
import com.libs.brut.androlib.AndrolibException;

public class ResDimenValue extends ResIntValue {
    public ResDimenValue(int value, String rawValue) {
        super(value, rawValue, "dimen");
    }

    @Override
    protected String encodeAsResXml() throws AndrolibException {
        return TypedValue.coerceToString(TypedValue.TYPE_DIMENSION, mValue);
    }
}
