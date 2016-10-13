package com.libs.brut.androlib.res.util;

import org.xmlpull.mxp1_serializer.MXSerializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public class ExtMXSerializer extends MXSerializer implements ExtXmlSerializer {
    @Override
    public void startDocument(String encoding, Boolean standalone) throws IOException, IllegalArgumentException, IllegalStateException {
        super.startDocument(encoding != null ? encoding : mDefaultEncoding, standalone);
        this.newLine();
    }

    @Override
    protected void writeAttributeValue(String value, Writer out) throws IOException {
        if (mIsDisabledAttrEscape) {
            out.write(value == null ? "" : value);
            return;
        }
        super.writeAttributeValue(value, out);
    }

    @Override
    public void setOutput(OutputStream os, String encoding) throws IOException {
        super.setOutput(os, encoding != null ? encoding : mDefaultEncoding);
    }

    @Override
    public Object getProperty(String name) throws IllegalArgumentException {
        if (PROPERTY_DEFAULT_ENCODING.equals(name)) {
            return mDefaultEncoding;
        }
        return super.getProperty(name);
    }

    @Override
    public void setProperty(String name, Object value) throws IllegalArgumentException, IllegalStateException {
        if (PROPERTY_DEFAULT_ENCODING.equals(name)) {
            mDefaultEncoding = (String) value;
        } else {
            super.setProperty(name, value);
        }
    }

    @Override
    public ExtXmlSerializer newLine() throws IOException {
        super.out.write(lineSeparator);
        return this;
    }

    @Override
    public void setDisabledAttrEscape(boolean disabled) {
        mIsDisabledAttrEscape = disabled;
    }

    private String mDefaultEncoding;
    private boolean mIsDisabledAttrEscape = false;

}
