package com.libs.brut.androlib.res.data.value;

import com.libs.brut.androlib.AndrolibException;
import com.libs.brut.androlib.res.data.ResResource;
import com.libs.brut.androlib.res.xml.ResXmlEncoders;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.util.regex.Pattern;

public class ResStringValue extends ResScalarValue {

    public ResStringValue(String value, int rawValue) {
        this(value, rawValue, "string");
    }

    public ResStringValue(String value, int rawValue, String type) {
        super(type, rawValue, value);
    }

    @Override
    public String encodeAsResXmlAttr() {
        return checkIfStringIsNumeric(ResXmlEncoders.encodeAsResXmlAttr(mRawValue));
    }

    @Override
    public String encodeAsResXmlItemValue() {
        return ResXmlEncoders.enumerateNonPositionalSubstitutionsIfRequired(ResXmlEncoders.encodeAsXmlValue(mRawValue));
    }

    @Override
    public String encodeAsResXmlValue() {
        return ResXmlEncoders.encodeAsXmlValue(mRawValue);
    }

    @Override
    protected String encodeAsResXml() throws AndrolibException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void serializeExtraXmlAttrs(XmlSerializer serializer, ResResource res) throws IOException {
        if (ResXmlEncoders.hasMultipleNonPositionalSubstitutions(mRawValue)) {
            serializer.attribute(null, "formatted", "false");
        }
    }

    private String checkIfStringIsNumeric(String val) {
        if (val == null || val.isEmpty()) {
            return val;
        }
        return allDigits.matcher(val).matches() ? "\\ " + val : val;
    }

    private static Pattern allDigits = Pattern.compile("\\d{9,}");
}
