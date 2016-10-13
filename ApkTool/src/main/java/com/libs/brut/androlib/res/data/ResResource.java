package com.libs.brut.androlib.res.data;

import com.libs.brut.androlib.AndrolibException;
import com.libs.brut.androlib.res.data.value.ResValue;

public class ResResource {
    private final ResType mConfig;
    private final ResResSpec mResSpec;
    private final ResValue mValue;

    public ResResource(ResType config, ResResSpec spec, ResValue value) {
        this.mConfig = config;
        this.mResSpec = spec;
        this.mValue = value;
    }

    public String getFilePath() {
        return mResSpec.getType().getName() + mConfig.getFlags().getQualifiers() + "/" + mResSpec.getName();
    }

    public ResType getConfig() {
        return mConfig;
    }

    public ResResSpec getResSpec() {
        return mResSpec;
    }

    public ResValue getValue() {
        return mValue;
    }

    public void replace(ResValue value) throws AndrolibException {
        ResResource res = new ResResource(mConfig, mResSpec, value);
        mConfig.addResource(res, true);
        mResSpec.addResource(res, true);
    }

    @Override
    public String toString() {
        return getFilePath();
    }
}
