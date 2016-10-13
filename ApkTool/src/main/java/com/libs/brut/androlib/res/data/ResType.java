package com.libs.brut.androlib.res.data;

import com.libs.brut.androlib.AndrolibException;
import com.libs.brut.androlib.err.UndefinedResObject;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class ResType {
    private final ResConfigFlags mFlags;
    private final Map<ResResSpec, ResResource> mResources = new LinkedHashMap<ResResSpec, ResResource>();

    public ResType(ResConfigFlags flags) {
        this.mFlags = flags;
    }

    public Set<ResResource> listResources() {
        return new LinkedHashSet<ResResource>(mResources.values());
    }

    public ResResource getResource(ResResSpec spec) throws AndrolibException {
        ResResource res = mResources.get(spec);
        if (res == null) {
            throw new UndefinedResObject(String.format("resource: spec=%s, config=%s", spec, this));
        }
        return res;
    }

    public Set<ResResSpec> listResSpecs() {
        return mResources.keySet();
    }

    public ResConfigFlags getFlags() {
        return mFlags;
    }

    public void addResource(ResResource res) throws AndrolibException {
        addResource(res, false);
    }

    public void removeResource(ResResource res) throws AndrolibException {
        ResResSpec spec = res.getResSpec();
        mResources.remove(spec);
    }

    public void addResource(ResResource res, boolean overwrite) throws AndrolibException {
        ResResSpec spec = res.getResSpec();
        if (mResources.put(spec, res) != null && !overwrite) {
            throw new AndrolibException(String.format("Multiple resources: spec=%s, config=%s", spec, this));
        }
    }

    @Override
    public String toString() {
        return mFlags.toString();
    }
}
