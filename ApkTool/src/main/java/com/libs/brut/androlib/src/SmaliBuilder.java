package com.libs.brut.androlib.src;

import com.libs.brut.androlib.AndrolibException;
import com.libs.brut.androlib.mod.SmaliMod;
import com.libs.brut.androlib.res.util.ExtFile;

import org.antlr.runtime.RecognitionException;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.jf.dexlib2.writer.io.FileDataStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import J.dir.DirectoryException;

public class SmaliBuilder {
    public static void build(ExtFile smaliDir, File dexFile, int apiLevel) throws AndrolibException {
        new SmaliBuilder(smaliDir, dexFile, apiLevel).build();
    }

    public static void build(ExtFile smaliDir, File dexFile) throws AndrolibException {
        new SmaliBuilder(smaliDir, dexFile, 0).build();
    }

    private SmaliBuilder(ExtFile smaliDir, File dexFile, int apiLevel) {
        mSmaliDir = smaliDir;
        mDexFile = dexFile;
        mApiLevel = apiLevel;
    }

    private void build() throws AndrolibException {
        try {
            DexBuilder dexBuilder;
            if (mApiLevel > 0) {
                dexBuilder = DexBuilder.makeDexBuilder(Opcodes.forApi(mApiLevel));
            } else {
                dexBuilder = DexBuilder.makeDexBuilder();
            }

            for (String fileName : mSmaliDir.getDirectory().getFiles(true)) {
                buildFile(fileName, dexBuilder);
            }
            dexBuilder.writeTo(new FileDataStore(new File(mDexFile.getAbsolutePath())));
        } catch (IOException | DirectoryException ex) {
            throw new AndrolibException(ex);
        }
    }

    private void buildFile(String fileName, DexBuilder dexBuilder) throws AndrolibException, IOException {
        File inFile = new File(mSmaliDir, fileName);
        InputStream inStream = new FileInputStream(inFile);

        if (fileName.endsWith(".smali")) {
            try {
                if (!SmaliMod.assembleSmaliFile(inFile, dexBuilder, false, false)) {
                    throw new AndrolibException("Could not smali file: " + fileName);
                }
            } catch (IOException | RecognitionException ex) {
                throw new AndrolibException(ex);
            }
        } else {
            LOGGER.warning("Unknown file type, ignoring: " + inFile);
        }
        inStream.close();
    }

    private final ExtFile mSmaliDir;
    private final File mDexFile;
    private int mApiLevel = 0;

    private final static Logger LOGGER = Logger.getLogger(SmaliBuilder.class.getName());
}
