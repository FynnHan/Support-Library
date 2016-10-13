package com.libs.brut.androlib.res.util;

import java.io.IOException;
import org.xmlpull.v1.XmlSerializer;

public interface ExtXmlSerializer extends XmlSerializer {

    ExtXmlSerializer newLine() throws IOException;

    void setDisabledAttrEscape(boolean disabled);

    String PROPERTY_SERIALIZER_INDENTATION = "http://xmlpull.org/v1/doc/properties.html#serializer-indentation";
    String PROPERTY_SERIALIZER_LINE_SEPARATOR = "http://xmlpull.org/v1/doc/properties.html#serializer-line-separator";
    String PROPERTY_DEFAULT_ENCODING = "DEFAULT_ENCODING";
}
