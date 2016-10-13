package com.libs.brut.androlib.res.xml;

import com.libs.brut.androlib.AndrolibException;

public interface ResXmlEncodable {
    String encodeAsResXmlAttr() throws AndrolibException;

    String encodeAsResXmlValue() throws AndrolibException;
}
