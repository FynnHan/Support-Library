package com.libs.brut.androlib.res.data;

import com.libs.brut.androlib.AndrolibException;
import com.libs.brut.androlib.err.UndefinedResObject;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public final class ResTypeSpec {
    private final String mName;
    private final Map<String, ResResSpec> mResSpecs = new LinkedHashMap<String, ResResSpec>();

    private final ResTable mResTable;
    private final ResPackage mPackage;

    private final int mId;
    private final int mEntryCount;

    public ResTypeSpec(String name, ResTable resTable, ResPackage package_, int id, int entryCount) {
        this.mName = name;
        this.mResTable = resTable;
        this.mPackage = package_;
        this.mId = id;
        this.mEntryCount = entryCount;
    }

    public String getName() {
        return mName;
    }

    public int getId() {
        return mId;
    }

    public int getEntryCount() {
        return mEntryCount;
    }

    public boolean isString() {
        return mName.equalsIgnoreCase("string");
    }

    public Set<ResResSpec> listResSpecs() {
        return new LinkedHashSet<ResResSpec>(mResSpecs.values());
    }

    public ResResSpec getResSpec(String name) throws AndrolibException {
        ResResSpec spec = mResSpecs.get(name);
        if (spec == null) {
            throw new UndefinedResObject(String.format("resource spec: %s/%s", getName(), name));
        }
        return spec;
    }

    public void removeResSpec(ResResSpec spec) throws AndrolibException {
        mResSpecs.remove(spec.getName());
    }

    public void addResSpec(ResResSpec spec) throws AndrolibException {
        if (mResSpecs.put(spec.getName(), spec) != null) {
            throw new AndrolibException(String.format("Multiple res specs: %s/%s", getName(), spec.getName()));
        }
    }

    @Override
    public String toString() {
        return mName;
    }
}
