package com.libs.brut.androlib.res.data.value;

import com.libs.brut.androlib.AndrolibException;
import com.libs.brut.androlib.res.data.ResResource;
import com.libs.brut.androlib.res.xml.ResValuesXmlSerializable;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.util.Arrays;

import J.util.Duo;

public class ResArrayValue extends ResBagValue implements ResValuesXmlSerializable {
    private String mRawItems;

    ResArrayValue(ResReferenceValue parent, Duo<Integer, ResScalarValue>[] items) {
        super(parent);

        mItems = new ResScalarValue[items.length];
        for (int i = 0; i < items.length; i++) {
            mItems[i] = items[i].m2;
        }
    }

    public ResArrayValue(ResReferenceValue parent, ResScalarValue[] items) {
        super(parent);
        mItems = items;
    }

    @Override
    public void serializeToResValuesXml(XmlSerializer serializer, ResResource res) throws IOException, AndrolibException {
        String type = getType();
        type = (type == null ? "" : type + "-") + "array";
        serializer.startTag(null, type);
        serializer.attribute(null, "name", res.getResSpec().getName());

        // lets check if we need to add formatted="false" to this array
        for (int i = 0; i < mItems.length; i++) {
            if (mItems[i].hasMultipleNonPositionalSubstitutions()) {
                serializer.attribute(null, "formatted", "false");
                break;
            }
        }

        // add <item>'s
        for (int i = 0; i < mItems.length; i++) {
            serializer.startTag(null, "item");
            serializer.text(mItems[i].encodeAsResXmlNonEscapedItemValue());
            serializer.endTag(null, "item");
        }
        serializer.endTag(null, type);
    }

    public String getType() throws AndrolibException {
        if (mItems.length == 0) {
            return null;
        }
        String type = mItems[0].getType();
        for (int i = 0; i < mItems.length; i++) {
            if (mItems[i].encodeAsResXmlItemValue().startsWith("@string")) {
                return "string";
            } else if (mItems[i].encodeAsResXmlItemValue().startsWith("@drawable")) {
                return null;
            } else if (mItems[i].encodeAsResXmlItemValue().startsWith("@integer")) {
                return "integer";
            } else if (!"string".equals(type) && !"integer".equals(type)) {
                return null;
            } else if (!type.equals(mItems[i].getType())) {
                return null;
            }
        }
        if (!Arrays.asList(AllowedArrayTypes).contains(type)) {
            return "string";
        }
        return type;
    }

    private final ResScalarValue[] mItems;
    private final String AllowedArrayTypes[] = {"string", "integer"};

    public static final int BAG_KEY_ARRAY_START = 0x02000000;
}
