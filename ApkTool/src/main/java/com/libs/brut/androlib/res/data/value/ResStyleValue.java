package com.libs.brut.androlib.res.data.value;

import com.libs.brut.androlib.AndrolibException;
import com.libs.brut.androlib.res.data.ResResSpec;
import com.libs.brut.androlib.res.data.ResResource;
import com.libs.brut.androlib.res.xml.ResValuesXmlSerializable;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

import J.util.Duo;

public class ResStyleValue extends ResBagValue implements ResValuesXmlSerializable {

    ResStyleValue(ResReferenceValue parent, Duo<Integer, ResScalarValue>[] items, ResValueFactory factory) {
        super(parent);

        mItems = new Duo[items.length];
        for (int i = 0; i < items.length; i++) {
            mItems[i] = new Duo<ResReferenceValue, ResScalarValue>(factory.newReference(items[i].m1, null), items[i].m2);
        }
    }

    @Override
    public void serializeToResValuesXml(XmlSerializer serializer, ResResource res) throws IOException, AndrolibException {
        serializer.startTag(null, "style");
        serializer.attribute(null, "name", res.getResSpec().getName());
        if (!mParent.isNull() && !mParent.referentIsNull()) {
            serializer.attribute(null, "parent", mParent.encodeAsResXmlAttr());
        } else if (res.getResSpec().getName().indexOf('.') != -1) {
            serializer.attribute(null, "parent", "");
        }
        for (int i = 0; i < mItems.length; i++) {
            ResResSpec spec = mItems[i].m1.getReferent();
            String name = null;
            String value = null;

            String resource = spec.getDefaultResource().getValue().toString();
            // hacky-fix remove bad ReferenceVars
            if (resource.contains("ResReferenceValue@")) {
                continue;
            } else if (resource.contains("ResStringValue@") || resource.contains("ResStyleValue@") ||
                    resource.contains("ResBoolValue@")) {
                name = "@" + spec.getFullName(res.getResSpec().getPackage(), false);
            } else {
                ResAttr attr = (ResAttr) spec.getDefaultResource().getValue();
                value = attr.convertToResXmlFormat(mItems[i].m2);
                name = spec.getFullName(res.getResSpec().getPackage(), true);
            }

            if (value == null) {
                value = mItems[i].m2.encodeAsResXmlValue();
            }

            if (value == null) {
                continue;
            }

            serializer.startTag(null, "item");
            serializer.attribute(null, "name", name);
            serializer.text(value);
            serializer.endTag(null, "item");
        }
        serializer.endTag(null, "style");
    }

    private final Duo<ResReferenceValue, ResScalarValue>[] mItems;
}
