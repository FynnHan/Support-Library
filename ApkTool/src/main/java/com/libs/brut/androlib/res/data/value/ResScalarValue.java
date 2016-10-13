package com.libs.brut.androlib.res.data.value;

import com.libs.brut.androlib.AndrolibException;
import com.libs.brut.androlib.res.data.ResResource;
import com.libs.brut.androlib.res.xml.ResValuesXmlSerializable;
import com.libs.brut.androlib.res.xml.ResXmlEncodable;
import com.libs.brut.androlib.res.xml.ResXmlEncoders;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

public abstract class ResScalarValue extends ResIntBasedValue implements ResXmlEncodable, ResValuesXmlSerializable {
    protected final String mType;
    protected final String mRawValue;

    protected ResScalarValue(String type, int rawIntValue, String rawValue) {
        super(rawIntValue);
        mType = type;
        mRawValue = rawValue;
    }

    @Override
    public String encodeAsResXmlAttr() throws AndrolibException {
        if (mRawValue != null) {
            return mRawValue;
        }
        return encodeAsResXml();
    }

    public String encodeAsResXmlItemValue() throws AndrolibException {
        return encodeAsResXmlValue();
    }

    @Override
    public String encodeAsResXmlValue() throws AndrolibException {
        if (mRawValue != null) {
            return mRawValue;
        }
        return encodeAsResXml();
    }

    public String encodeAsResXmlNonEscapedItemValue() throws AndrolibException {
        return encodeAsResXmlValue().replace("&amp;", "&").replace("&lt;", "<");
    }

    public boolean hasMultipleNonPositionalSubstitutions() throws AndrolibException {
        return ResXmlEncoders.hasMultipleNonPositionalSubstitutions(mRawValue);
    }

    @Override
    public void serializeToResValuesXml(XmlSerializer serializer,
                                        ResResource res) throws IOException, AndrolibException {
        String type = res.getResSpec().getType().getName();
        boolean item = !"reference".equals(mType) && !type.equals(mType);

        String body = encodeAsResXmlValue();

        // check for resource reference
        if (!type.equalsIgnoreCase("color")) {
            if (body.contains("@")) {
                if (!res.getFilePath().contains("string")) {
                    item = true;
                }
            }
        }

        // check for using attrib as node or item
        String tagName = item ? "item" : type;

        serializer.startTag(null, tagName);
        if (item) {
            serializer.attribute(null, "type", type);
        }
        serializer.attribute(null, "name", res.getResSpec().getName());

        serializeExtraXmlAttrs(serializer, res);

        if (!body.isEmpty()) {
            serializer.ignorableWhitespace(body);
        }

        serializer.endTag(null, tagName);
    }

    public String getType() {
        return mType;
    }

    protected void serializeExtraXmlAttrs(XmlSerializer serializer,
                                          ResResource res) throws IOException {
    }

    protected abstract String encodeAsResXml() throws AndrolibException;
}
