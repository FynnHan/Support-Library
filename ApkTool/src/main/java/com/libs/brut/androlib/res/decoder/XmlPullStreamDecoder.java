package com.libs.brut.androlib.res.decoder;

import com.libs.brut.androlib.AndrolibException;
import com.libs.brut.androlib.res.data.ResTable;
import com.libs.brut.androlib.res.util.ExtXmlSerializer;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.wrapper.XmlPullParserWrapper;
import org.xmlpull.v1.wrapper.XmlPullWrapperFactory;
import org.xmlpull.v1.wrapper.XmlSerializerWrapper;
import org.xmlpull.v1.wrapper.classic.StaticXmlSerializerWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

public class XmlPullStreamDecoder implements ResStreamDecoder {

    public XmlPullStreamDecoder(XmlPullParser parser, ExtXmlSerializer serializer) {
        this.mParser = parser;
        this.mSerial = serializer;
    }

    @Override
    public void decode(InputStream in, OutputStream out) throws AndrolibException {
        try {
            XmlPullWrapperFactory factory = XmlPullWrapperFactory.newInstance();
            XmlPullParserWrapper par = factory.newPullParserWrapper(mParser);
            final ResTable resTable = ((AXmlResourceParser) mParser).getAttrDecoder().getCurrentPackage().getResTable();

            XmlSerializerWrapper ser = new StaticXmlSerializerWrapper(mSerial, factory) {
                boolean hideSdkInfo = false;
                boolean hidePackageInfo = false;

                @Override
                public void event(XmlPullParser pp)
                        throws XmlPullParserException, IOException {
                    int type = pp.getEventType();

                    if (type == XmlPullParser.START_TAG) {
                        if ("manifest".equalsIgnoreCase(pp.getName())) {
                            try {
                                hidePackageInfo = parseManifest(pp);
                            } catch (AndrolibException ignored) {
                            }
                        } else if ("uses-sdk".equalsIgnoreCase(pp.getName())) {
                            try {
                                hideSdkInfo = parseAttr(pp);
                                if (hideSdkInfo) {
                                    return;
                                }
                            } catch (AndrolibException ignored) {
                            }
                        }
                    } else if (hideSdkInfo && type == XmlPullParser.END_TAG
                            && "uses-sdk".equalsIgnoreCase(pp.getName())) {
                        return;
                    } else if (hidePackageInfo && type == XmlPullParser.END_TAG
                            && "manifest".equalsIgnoreCase(pp.getName())) {
                        super.event(pp);
                        return;
                    }
                    super.event(pp);
                }

                private boolean parseManifest(XmlPullParser pp)
                        throws AndrolibException {
                    String attr_name;

                    // read <manifest> for package:
                    for (int i = 0; i < pp.getAttributeCount(); i++) {
                        attr_name = pp.getAttributeName(i);

                        if (attr_name.equalsIgnoreCase(("package"))) {
                            resTable.setPackageRenamed(pp.getAttributeValue(i));
                        } else if (attr_name.equalsIgnoreCase("versionCode")) {
                            resTable.setVersionCode(pp.getAttributeValue(i));
                        } else if (attr_name.equalsIgnoreCase("versionName")) {
                            resTable.setVersionName(pp.getAttributeValue(i));
                        }
                    }
                    return true;
                }

                private boolean parseAttr(XmlPullParser pp)
                        throws AndrolibException {
                    for (int i = 0; i < pp.getAttributeCount(); i++) {
                        final String a_ns = "http://schemas.android.com/apk/res/android";
                        String ns = pp.getAttributeNamespace(i);

                        if (a_ns.equalsIgnoreCase(ns)) {
                            String name = pp.getAttributeName(i);
                            String value = pp.getAttributeValue(i);
                            if (name != null && value != null) {
                                if (name.equalsIgnoreCase("minSdkVersion")
                                        || name.equalsIgnoreCase("targetSdkVersion")
                                        || name.equalsIgnoreCase("maxSdkVersion")) {
                                    resTable.addSdkInfo(name, value);
                                } else {
                                    resTable.clearSdkInfo();
                                    return false; // Found unknown flags
                                }
                            }
                        } else {
                            resTable.clearSdkInfo();

                            if (i >= pp.getAttributeCount()) {
                                return false; // Found unknown flags
                            }
                        }
                    }

                    return !resTable.getAnalysisMode();
                }
            };

            par.setInput(in, null);
            ser.setOutput(out, null);

            while (par.nextToken() != XmlPullParser.END_DOCUMENT) {
                ser.event(par);
            }
            ser.flush();
        } catch (XmlPullParserException ex) {
            throw new AndrolibException("Could not decode XML", ex);
        } catch (IOException ex) {
            throw new AndrolibException("Could not decode XML", ex);
        }
    }

    public void decodeManifest(InputStream in, OutputStream out)
            throws AndrolibException {
        decode(in, out);
    }

    private final XmlPullParser mParser;
    private final ExtXmlSerializer mSerial;

    private final static Logger LOGGER = Logger.getLogger(XmlPullStreamDecoder.class.getName());
}
