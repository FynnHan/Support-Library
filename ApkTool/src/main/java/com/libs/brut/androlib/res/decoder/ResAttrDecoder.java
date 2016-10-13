package com.libs.brut.androlib.res.decoder;

import com.libs.brut.androlib.AndrolibException;
import com.libs.brut.androlib.err.UndefinedResObject;
import com.libs.brut.androlib.res.data.ResPackage;
import com.libs.brut.androlib.res.data.ResResSpec;
import com.libs.brut.androlib.res.data.value.ResAttr;
import com.libs.brut.androlib.res.data.value.ResScalarValue;

public class ResAttrDecoder {
    public String decode(int type, int value, String rawValue, int attrResId) throws AndrolibException {
        ResScalarValue resValue = mCurrentPackage.getValueFactory().factory(type, value, rawValue);

        String decoded = null;
        if (attrResId > 0) {
            try {
                ResAttr attr = (ResAttr) getCurrentPackage().getResTable().getResSpec(attrResId).getDefaultResource().getValue();

                decoded = attr.convertToResXmlFormat(resValue);
            } catch (UndefinedResObject | ClassCastException ex) {
                // ignored
            }
        }

        return decoded != null ? decoded : resValue.encodeAsResXmlAttr();
    }

    public String decodeManifestAttr(int attrResId) throws AndrolibException {
        if (attrResId != 0) {
            ResResSpec resResSpec = getCurrentPackage().getResTable().getResSpec(attrResId);

            if (resResSpec != null) {
                return resResSpec.getName();
            }
        }
        return null;
    }

    public ResPackage getCurrentPackage() throws AndrolibException {
        if (mCurrentPackage == null) {
            throw new AndrolibException("Current package not set");
        }
        return mCurrentPackage;
    }

    public void setCurrentPackage(ResPackage currentPackage) {
        mCurrentPackage = currentPackage;
    }

    private ResPackage mCurrentPackage;
}
