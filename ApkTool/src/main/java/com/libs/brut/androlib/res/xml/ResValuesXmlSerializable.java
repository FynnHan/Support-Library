package com.libs.brut.androlib.res.xml;

import com.libs.brut.androlib.AndrolibException;
import com.libs.brut.androlib.res.data.ResResource;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

public interface ResValuesXmlSerializable {
    void serializeToResValuesXml(XmlSerializer serializer, ResResource res) throws IOException, AndrolibException;
}
