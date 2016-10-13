package com.libs.brut.androlib.res.data.value;

import com.libs.brut.androlib.AndrolibException;
import com.libs.brut.androlib.res.data.ResResource;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import J.util.Duo;

public class ResEnumAttr extends ResAttr {
    ResEnumAttr(ResReferenceValue parent, int type, Integer min, Integer max, Boolean l10n, Duo<ResReferenceValue, ResIntValue>[] items) {
        super(parent, type, min, max, l10n);
        mItems = items;
    }

    @Override
    public String convertToResXmlFormat(ResScalarValue value)
            throws AndrolibException {
        if (value instanceof ResIntValue) {
            String ret = decodeValue(((ResIntValue) value).getValue());
            if (ret != null) {
                return ret;
            }
        }
        return super.convertToResXmlFormat(value);
    }

    @Override
    protected void serializeBody(XmlSerializer serializer, ResResource res) throws AndrolibException, IOException {
        for (Duo<ResReferenceValue, ResIntValue> duo : mItems) {
            int intVal = duo.m2.getValue();

            serializer.startTag(null, "enum");
            serializer.attribute(null, "name", duo.m1.getReferent().getName());
            serializer.attribute(null, "value", String.valueOf(intVal));
            serializer.endTag(null, "enum");
        }
    }

    private String decodeValue(int value) throws AndrolibException {
        String value2 = mItemsCache.get(value);
        if (value2 == null) {
            ResReferenceValue ref = null;
            for (Duo<ResReferenceValue, ResIntValue> duo : mItems) {
                if (duo.m2.getValue() == value) {
                    ref = duo.m1;
                    break;
                }
            }
            if (ref != null) {
                value2 = ref.getReferent().getName();
                mItemsCache.put(value, value2);
            }
        }
        return value2;
    }

    private final Duo<ResReferenceValue, ResIntValue>[] mItems;
    private final Map<Integer, String> mItemsCache = new HashMap<Integer, String>();
}
