package com.libs.brut.androlib;

import com.google.common.base.Strings;

import com.libs.brut.androlib.err.InFileNotFoundException;
import com.libs.brut.androlib.err.OutDirExistsException;
import com.libs.brut.androlib.err.UndefinedResObject;
import com.libs.brut.androlib.meta.MetaInfo;
import com.libs.brut.androlib.meta.PackageInfo;
import com.libs.brut.androlib.meta.UsesFramework;
import com.libs.brut.androlib.meta.VersionInfo;
import com.libs.brut.androlib.res.AndrolibResources;
import com.libs.brut.androlib.res.data.ResPackage;
import com.libs.brut.androlib.res.data.ResTable;
import com.libs.brut.androlib.res.util.ExtFile;
import com.libs.brut.androlib.res.xml.ResXmlPatcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import J.common.BrutException;
import J.dir.DirectoryException;
import J.util.OS;

public class ApkDecoder {
    public ApkDecoder() {
        this(new Androlib());
    }

    public ApkDecoder(Androlib androlib) {
        mAndrolib = androlib;
    }

    public ApkDecoder(File apkFile) {
        this(apkFile, new Androlib());
    }

    public ApkDecoder(File apkFile, Androlib androlib) {
        mAndrolib = androlib;
        setApkFile(apkFile);
    }

    public void setApkFile(File apkFile) {
        mApkFile = new ExtFile(apkFile);
        mResTable = null;
    }

    public void setOutDir(File outDir) throws AndrolibException {
        mOutDir = outDir;
    }

    public void setApi(int api) {
        mApi = api;
    }

    public void decode() throws AndrolibException, IOException, DirectoryException {
        File outDir = getOutDir();
        AndrolibResources.sKeepBroken = mKeepBrokenResources;

        // 如果输出目录不是因为删除而不存在就抛出异常
        if (!mForceDelete && outDir.exists()) {
            throw new OutDirExistsException();
        }

        // 如果apk文件不是文件类型或者不能读也抛出异常
        if (!mApkFile.isFile() || !mApkFile.canRead()) {
            throw new InFileNotFoundException();
        }

        try {
            OS.rmdir(outDir);
        } catch (BrutException ex) {
            throw new AndrolibException(ex);
        }
        outDir.mkdirs();

        LOGGER.info("Using Apktool " + Androlib.getVersion() + " on " + mApkFile.getName());

        // 如果需要解析资源文件的话
        if (hasResources()) {
            switch (mDecodeResources) {
                case DECODE_RESOURCES_NONE:
                    // 解析非arsc格式的文件，一般是libs,assets,raw目录下的文件，直接copy就可以了
                    mAndrolib.decodeResourcesRaw(mApkFile, outDir);
                    break;
                case DECODE_RESOURCES_FULL:
                    setTargetSdkVersion();
                    setAnalysisMode(mAnalysisMode, true);// 参数为true，影响mResTable的取值

                    // 如果需要解析AndroidManifest.xml的话就开始解析
                    if (hasManifest()) {
                        mAndrolib.decodeManifestWithResources(mApkFile, outDir, getResTable());
                    }
                    // 开始解析resource.arsc文件
                    mAndrolib.decodeResourcesFull(mApkFile, outDir, getResTable());
                    break;
            }
        } else {
            // if there's no resources.asrc, decode the manifest without looking
            // up attribute references
            if (hasManifest()) {
                switch (mDecodeResources) {
                    case DECODE_RESOURCES_NONE:
                        mAndrolib.decodeManifestRaw(mApkFile, outDir);
                        break;
                    case DECODE_RESOURCES_FULL:
                        mAndrolib.decodeManifestFull(mApkFile, outDir, getResTable());
                        break;
                }
            }
        }

        if (hasSources()) {
            switch (mDecodeSources) {
                case DECODE_SOURCES_NONE:
                    mAndrolib.decodeSourcesRaw(mApkFile, outDir, "classes.dex");
                    break;
                case DECODE_SOURCES_SMALI:
                    // 解析dex文件，变成smali源码，如果不需要解析的话，直接copy classes.dex即可
                    mAndrolib.decodeSourcesSmali(mApkFile, outDir, "classes.dex", mBakDeb, mApi);
                    break;
            }
        }

        // 现在很多大型的apk都有分包的功能，就是包含多个dex文件，这里做了多个dex的解析
        if (hasMultipleSources()) {
            // foreach unknown dex file in root, lets disassemble it
            Set<String> files = mApkFile.getDirectory().getFiles(true);
            for (String file : files) {
                if (file.endsWith(".dex")) {
                    if (!file.equalsIgnoreCase("classes.dex")) {
                        switch (mDecodeSources) {
                            case DECODE_SOURCES_NONE:
                                mAndrolib.decodeSourcesRaw(mApkFile, outDir, file);
                                break;
                            case DECODE_SOURCES_SMALI:
                                mAndrolib.decodeSourcesSmali(mApkFile, outDir, file, mBakDeb, mApi);
                                break;
                        }
                    }
                }
            }
        }

        mAndrolib.decodeRawFiles(mApkFile, outDir);
        mAndrolib.decodeUnknownFiles(mApkFile, outDir, mResTable);
        mUncompressedFiles = new ArrayList<String>();
        mAndrolib.recordUncompressedFiles(mApkFile, mUncompressedFiles);
        mAndrolib.writeOriginalFiles(mApkFile, outDir);
        writeMetaFile();
    }

    public void setDecodeSources(short mode) throws AndrolibException {
        if (mode != DECODE_SOURCES_NONE && mode != DECODE_SOURCES_SMALI) {
            throw new AndrolibException("Invalid decode sources mode: " + mode);
        }
        mDecodeSources = mode;
    }

    public void setDecodeResources(short mode) throws AndrolibException {
        if (mode != DECODE_RESOURCES_NONE && mode != DECODE_RESOURCES_FULL) {
            throw new AndrolibException("Invalid decode resources mode");
        }
        mDecodeResources = mode;
    }

    public void setAnalysisMode(boolean mode, boolean pass) throws AndrolibException {
        mAnalysisMode = mode;

        // only set mResTable, once it exists
        if (pass) {
            if (mResTable == null) {
                mResTable = getResTable();
            }
            mResTable.setAnalysisMode(mode);
        }
    }

    public void setTargetSdkVersion() throws AndrolibException, IOException {
        if (mResTable == null) {
            mResTable = mAndrolib.getResTable(mApkFile);
        }

        Map<String, String> sdkInfo = mResTable.getSdkInfo();
        if (sdkInfo.get("targetSdkVersion") != null) {
            mApi = Integer.parseInt(sdkInfo.get("targetSdkVersion"));
        }
    }

    public void setBaksmaliDebugMode(boolean bakdeb) {
        mBakDeb = bakdeb;
    }

    public void setForceDelete(boolean forceDelete) {
        mForceDelete = forceDelete;
    }

    public void setFrameworkTag(String tag) throws AndrolibException {
        mAndrolib.apkOptions.frameworkTag = tag;
    }

    public void setKeepBrokenResources(boolean keepBrokenResources) {
        mKeepBrokenResources = keepBrokenResources;
    }

    public void setFrameworkDir(String dir) {
        mAndrolib.apkOptions.frameworkFolderLocation = dir;
    }

    public ResTable getResTable() throws AndrolibException {
        if (mResTable == null) {
            boolean hasResources = hasResources();
            boolean hasManifest = hasManifest();
            if (!(hasManifest || hasResources)) {
                throw new AndrolibException("Apk doesn't contain either AndroidManifest.xml file or resources.arsc file");
            }
            mResTable = mAndrolib.getResTable(mApkFile, hasResources);
        }
        return mResTable;
    }

    public boolean hasSources() throws AndrolibException {
        try {
            return mApkFile.getDirectory().containsFile("classes.dex");
        } catch (DirectoryException ex) {
            throw new AndrolibException(ex);
        }
    }

    public boolean hasMultipleSources() throws AndrolibException {
        try {
            Set<String> files = mApkFile.getDirectory().getFiles(false);
            for (String file : files) {
                if (file.endsWith(".dex")) {
                    if (!file.equalsIgnoreCase("classes.dex")) {
                        return true;
                    }
                }
            }

            return false;
        } catch (DirectoryException ex) {
            throw new AndrolibException(ex);
        }
    }

    public boolean hasManifest() throws AndrolibException {
        try {
            return mApkFile.getDirectory().containsFile("AndroidManifest.xml");
        } catch (DirectoryException ex) {
            throw new AndrolibException(ex);
        }
    }

    public boolean hasResources() throws AndrolibException {
        try {
            return mApkFile.getDirectory().containsFile("resources.arsc");
        } catch (DirectoryException ex) {
            throw new AndrolibException(ex);
        }
    }

    public final static short DECODE_SOURCES_NONE = 0x0000;
    public final static short DECODE_SOURCES_SMALI = 0x0001;

    public final static short DECODE_RESOURCES_NONE = 0x0100;
    public final static short DECODE_RESOURCES_FULL = 0x0101;

    private File getOutDir() throws AndrolibException {
        if (mOutDir == null) {
            throw new AndrolibException("Out dir not set");
        }
        return mOutDir;
    }

    private void writeMetaFile() throws AndrolibException {
        MetaInfo meta = new MetaInfo();
        meta.version = Androlib.getVersion();
        meta.apkFileName = mApkFile.getName();

        // 如果要反编译资源并且有资源文件或者manifest.xml文件
        if (mDecodeResources != DECODE_RESOURCES_NONE && (hasManifest() || hasResources())) {
            meta.isFrameworkApk = mAndrolib.isFrameworkApk(getResTable());
            putUsesFramework(meta);
            putSdkInfo(meta);
            putPackageInfo(meta);
            putVersionInfo(meta);
            putSharedLibraryInfo(meta);
        }
        putUnknownInfo(meta);// 加入Unknown文件目录说明
        putFileCompressionInfo(meta);// 将meta数据写入apktool.xml，最终写入到输出目录（用于回编译）

        mAndrolib.writeMetaFile(mOutDir, meta);
    }

    private void putUsesFramework(MetaInfo meta) throws AndrolibException {
        Set<ResPackage> pkgs = getResTable().listFramePackages();
        if (pkgs.isEmpty()) {
            return;
        }

        Integer[] ids = new Integer[pkgs.size()];
        int i = 0;
        for (ResPackage pkg : pkgs) {
            ids[i++] = pkg.getId();
        }
        Arrays.sort(ids);

        meta.usesFramework = new UsesFramework();
        meta.usesFramework.ids = Arrays.asList(ids);

        if (mAndrolib.apkOptions.frameworkTag != null) {
            meta.usesFramework.tag = mAndrolib.apkOptions.frameworkTag;
        }
    }

    private void putSdkInfo(MetaInfo meta) throws AndrolibException {
        Map<String, String> info = getResTable().getSdkInfo();
        if (info.size() > 0) {
            meta.sdkInfo = info;
        }
    }

    private void putPackageInfo(MetaInfo meta) throws AndrolibException {
        String renamed = getResTable().getPackageRenamed();
        String original = getResTable().getPackageOriginal();

        int id = getResTable().getPackageId();
        try {
            id = getResTable().getPackage(renamed).getId();
        } catch (UndefinedResObject ignored) {
        }

        if (Strings.isNullOrEmpty(original)) {
            return;
        }

        meta.packageInfo = new PackageInfo();

        // only put rename-manifest-package into apktool.yml, if the change will be required
        if (!renamed.equalsIgnoreCase(original)) {
            meta.packageInfo.renameManifestPackage = renamed;
        }
        meta.packageInfo.forcedPackageId = String.valueOf(id);
    }

    private void putVersionInfo(MetaInfo meta) throws AndrolibException {
        VersionInfo info = getResTable().getVersionInfo();
        String refValue = ResXmlPatcher.pullValueFromStrings(mOutDir, info.versionName);
        if (refValue != null) {
            info.versionName = refValue;
        }
        meta.versionInfo = info;
    }

    private void putUnknownInfo(MetaInfo meta) throws AndrolibException {
        meta.unknownFiles = mAndrolib.mResUnknownFiles.getUnknownFiles();
    }

    private void putFileCompressionInfo(MetaInfo meta) throws AndrolibException {
        if (!mUncompressedFiles.isEmpty()) {
            meta.doNotCompress = mUncompressedFiles;
        }
    }

    private void putSharedLibraryInfo(MetaInfo meta) throws AndrolibException {
        meta.sharedLibrary = mResTable.getSharedLibrary();
    }

    private final Androlib mAndrolib;

    private final static Logger LOGGER = Logger.getLogger(Androlib.class.getName());

    private ExtFile mApkFile;
    private File mOutDir;
    private ResTable mResTable;
    private short mDecodeSources = DECODE_SOURCES_SMALI;
    private short mDecodeResources = DECODE_RESOURCES_FULL;
    private boolean mForceDelete = false;
    private boolean mKeepBrokenResources = false;
    private boolean mBakDeb = true;
    private Collection<String> mUncompressedFiles;
    private boolean mAnalysisMode = false;
    private int mApi = 15;
}
