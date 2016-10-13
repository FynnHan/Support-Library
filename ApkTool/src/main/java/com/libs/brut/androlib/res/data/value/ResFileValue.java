package com.libs.brut.androlib.res.data.value;

import com.libs.brut.androlib.AndrolibException;

public class ResFileValue extends ResIntBasedValue {
    private final String mPath;

    public ResFileValue(String path, int rawIntValue) {
        super(rawIntValue);
        this.mPath = path;
    }

    public String getPath() {
        return mPath;
    }

    public String getStrippedPath() throws AndrolibException {
        if (mPath.startsWith("res/")) {
            return mPath.substring(4);
        }
        if (mPath.startsWith("r/") || mPath.startsWith("R/")) {
            return mPath.substring(2);
        }
        throw new AndrolibException("File path does not start with \"res/\" or \"r/\": " + mPath);
    }

    @Override
    public String toString() {
        return mPath;
    }
}
