package com.libs.brut.androlib;

import java.util.Collection;

public class ApkOptions {
    public boolean forceBuildAll = false;
    public boolean forceDeleteFramework = false;
    public boolean debugMode = false;
    public boolean verbose = false;
    public boolean copyOriginalFiles = false;
    public boolean updateFiles = false;
    public boolean isFramework = false;
    public boolean resourcesAreCompressed = false;
    public Collection<String> doNotCompress;

    public String frameworkFolderLocation = null;
    public String frameworkTag = null;
    public String aaptPath = "";
}
