package com.libs.brut.androlib.res.data.value;

import com.libs.brut.androlib.AndrolibException;
import com.libs.brut.androlib.res.data.ResResource;
import com.libs.brut.androlib.res.xml.ResValuesXmlSerializable;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

public class ResIdValue extends ResValue implements ResValuesXmlSerializable {
    @Override
    public void serializeToResValuesXml(XmlSerializer serializer,
                                        ResResource res) throws IOException, AndrolibException {
        serializer.startTag(null, "item");
        serializer
                .attribute(null, "type", res.getResSpec().getType().getName());
        serializer.attribute(null, "name", res.getResSpec().getName());
        serializer.endTag(null, "item");
    }
}
