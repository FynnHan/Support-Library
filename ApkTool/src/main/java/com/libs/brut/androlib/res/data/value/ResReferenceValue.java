package com.libs.brut.androlib.res.data.value;

import com.libs.brut.androlib.AndrolibException;
import com.libs.brut.androlib.err.UndefinedResObject;
import com.libs.brut.androlib.res.data.ResPackage;
import com.libs.brut.androlib.res.data.ResResSpec;

public class ResReferenceValue extends ResIntValue {
    private final ResPackage mPackage;
    private final boolean mTheme;

    public ResReferenceValue(ResPackage package_, int value, String rawValue) {
        this(package_, value, rawValue, false);
    }

    public ResReferenceValue(ResPackage package_, int value, String rawValue,
                             boolean theme) {
        super(value, rawValue, "reference");
        mPackage = package_;
        mTheme = theme;
    }

    @Override
    protected String encodeAsResXml() throws AndrolibException {
        if (isNull()) {
            return "@null";
        }

        ResResSpec spec = getReferent();
        if (spec == null) {
            return "@null";
        }
        boolean newId = spec.hasDefaultResource() && spec.getDefaultResource().getValue() instanceof ResIdValue;

        // generate the beginning to fix @android
        String mStart = (mTheme ? '?' : '@') + (newId ? "+" : "");

        return mStart + spec.getFullName(mPackage, mTheme && spec.getType().getName().equals("attr"));
    }

    public ResResSpec getReferent() throws AndrolibException {
        try {
            return mPackage.getResTable().getResSpec(getValue());
        } catch (UndefinedResObject ex) {
            return null;
        }
    }

    public boolean isNull() {
        return mValue == 0;
    }

    public boolean referentIsNull() throws AndrolibException {
        return getReferent() == null;
    }
}
