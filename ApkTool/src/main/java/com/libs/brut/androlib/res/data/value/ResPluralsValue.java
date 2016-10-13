package com.libs.brut.androlib.res.data.value;

import com.libs.brut.androlib.AndrolibException;
import com.libs.brut.androlib.res.data.ResResource;
import com.libs.brut.androlib.res.xml.ResValuesXmlSerializable;
import com.libs.brut.androlib.res.xml.ResXmlEncoders;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

import J.util.Duo;

public class ResPluralsValue extends ResBagValue implements ResValuesXmlSerializable {
    ResPluralsValue(ResReferenceValue parent,
                    Duo<Integer, ResScalarValue>[] items) {
        super(parent);

        mItems = new ResScalarValue[6];
        for (int i = 0; i < items.length; i++) {
            mItems[items[i].m1 - BAG_KEY_PLURALS_START] = items[i].m2;
        }
    }

    @Override
    public void serializeToResValuesXml(XmlSerializer serializer,
                                        ResResource res) throws IOException, AndrolibException {
        serializer.startTag(null, "plurals");
        serializer.attribute(null, "name", res.getResSpec().getName());
        for (int i = 0; i < mItems.length; i++) {
            ResScalarValue item = mItems[i];
            if (item == null) {
                continue;
            }

            ResScalarValue rawValue = item;

            serializer.startTag(null, "item");
            serializer.attribute(null, "quantity", QUANTITY_MAP[i]);
            serializer.text(ResXmlEncoders.enumerateNonPositionalSubstitutionsIfRequired(item.encodeAsResXmlValue()));
            serializer.endTag(null, "item");
        }
        serializer.endTag(null, "plurals");
    }

    private final ResScalarValue[] mItems;

    public static final int BAG_KEY_PLURALS_START = 0x01000004;
    public static final int BAG_KEY_PLURALS_END = 0x01000009;
    private static final String[] QUANTITY_MAP = new String[]{"other", "zero", "one", "two", "few", "many"};
}
