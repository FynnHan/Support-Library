package com.libs.brut.androlib.res.data.value;

import com.libs.brut.androlib.AndrolibException;
import com.libs.brut.androlib.res.data.ResResource;
import com.libs.brut.androlib.res.xml.ResValuesXmlSerializable;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

import J.util.Duo;

public class ResBagValue extends ResValue implements ResValuesXmlSerializable {
    protected final ResReferenceValue mParent;

    public ResBagValue(ResReferenceValue parent) {
        this.mParent = parent;
    }

    @Override
    public void serializeToResValuesXml(XmlSerializer serializer, ResResource res) throws IOException, AndrolibException {
        String type = res.getResSpec().getType().getName();
        if ("style".equals(type)) {
            new ResStyleValue(mParent, new Duo[0], null)
                    .serializeToResValuesXml(serializer, res);
            return;
        }
        if ("array".equals(type)) {
            new ResArrayValue(mParent, new Duo[0]).serializeToResValuesXml(
                    serializer, res);
            return;
        }
        if ("plurals".equals(type)) {
            new ResPluralsValue(mParent, new Duo[0]).serializeToResValuesXml(
                    serializer, res);
            return;
        }

        serializer.startTag(null, "item");
        serializer.attribute(null, "type", type);
        serializer.attribute(null, "name", res.getResSpec().getName());
        serializer.endTag(null, "item");
    }

    public ResReferenceValue getParent() {
        return mParent;
    }
}
