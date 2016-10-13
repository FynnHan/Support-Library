package com.libs.android.content.res;

import com.libs.android.util.AttributeSet;
import org.xmlpull.v1.XmlPullParser;

public interface XmlResourceParser extends XmlPullParser, AttributeSet {
    /**
     * Close this interface to the resource. Calls on the interface are no longer value after this call.
     */
    void close();
}
