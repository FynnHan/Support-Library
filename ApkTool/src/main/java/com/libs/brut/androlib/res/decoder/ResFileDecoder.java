package com.libs.brut.androlib.res.decoder;

import com.libs.brut.androlib.AndrolibException;
import com.libs.brut.androlib.err.CantFind9PatchChunk;
import com.libs.brut.androlib.res.data.ResResource;
import com.libs.brut.androlib.res.data.value.ResBoolValue;
import com.libs.brut.androlib.res.data.value.ResFileValue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import J.dir.DirUtil;
import J.dir.Directory;
import J.dir.DirectoryException;

public class ResFileDecoder {
    private final ResStreamDecoderContainer mDecoders;

    public ResFileDecoder(ResStreamDecoderContainer decoders) {
        this.mDecoders = decoders;
    }

    public void decode(ResResource res, Directory inDir, Directory outDir) throws AndrolibException {

        ResFileValue fileValue = (ResFileValue) res.getValue();
        String inFileName = fileValue.getStrippedPath();
        String outResName = res.getFilePath();
        String typeName = res.getResSpec().getType().getName();

        String ext = null;
        String outFileName;
        int extPos = inFileName.lastIndexOf(".");
        if (extPos == -1) {
            outFileName = outResName;
        } else {
            ext = inFileName.substring(extPos).toLowerCase();
            outFileName = outResName + ext;
        }

        try {
            if (typeName.equals("raw")) {
                decode(inDir, inFileName, outDir, outFileName, "raw");
                return;
            }
            if (typeName.equals("drawable") || typeName.equals("mipmap")) {
                if (inFileName.toLowerCase().endsWith(".9" + ext)) {
                    outFileName = outResName + ".9" + ext;

                    // check for htc .r.9.png
                    if (inFileName.toLowerCase().endsWith(".r.9" + ext)) {
                        outFileName = outResName + ".r.9" + ext;
                    }

                    // check for samsung qmg & spi
                    if (inFileName.toLowerCase().endsWith(".qmg") || inFileName.toLowerCase().endsWith(".spi")) {
                        copyRaw(inDir, outDir, outFileName);
                        return;
                    }

                    // check for xml 9 patches which are just xml files
                    if (inFileName.toLowerCase().endsWith(".xml")) {
                        decode(inDir, inFileName, outDir, outFileName, "xml");
                        return;
                    }

                    try {
                        decode(inDir, inFileName, outDir, outFileName, "9patch");
                        return;
                    } catch (CantFind9PatchChunk ex) {
                        LOGGER.log(
                                Level.WARNING,
                                String.format("Cant find 9patch chunk in file: \"%s\". Renaming it to *.png.", inFileName), ex);
                        outDir.removeFile(outFileName);
                        outFileName = outResName + ext;
                    }
                }
                if (!".xml".equals(ext)) {
                    decode(inDir, inFileName, outDir, outFileName, "raw");
                    return;
                }
            }

            decode(inDir, inFileName, outDir, outFileName, "xml");
        } catch (AndrolibException ex) {
            LOGGER.log(Level.SEVERE, String.format(
                    "Could not decode file, replacing by FALSE value: %s",
                    inFileName), ex);
            res.replace(new ResBoolValue(false, 0, null));
        }
    }

    public void decode(Directory inDir, String inFileName, Directory outDir, String outFileName, String decoder) throws AndrolibException {
        try (
                InputStream in = inDir.getFileInput(inFileName);
                OutputStream out = outDir.getFileOutput(outFileName)
        ) {
            mDecoders.decode(in, out, decoder);
        } catch (DirectoryException | IOException ex) {
            throw new AndrolibException(ex);
        }
    }

    public void copyRaw(Directory inDir, Directory outDir, String filename) throws AndrolibException {
        try {
            DirUtil.copyToDir(inDir, outDir, filename);
        } catch (DirectoryException ex) {
            throw new AndrolibException(ex);
        }
    }

    public void decodeManifest(Directory inDir, String inFileName, Directory outDir, String outFileName) throws AndrolibException {
        try (
                InputStream in = inDir.getFileInput(inFileName);
                OutputStream out = outDir.getFileOutput(outFileName)
        ) {
            ((XmlPullStreamDecoder) mDecoders.getDecoder("xml")).decodeManifest(in, out);
        } catch (DirectoryException | IOException ex) {
            throw new AndrolibException(ex);
        }
    }

    private final static Logger LOGGER = Logger.getLogger(ResFileDecoder.class.getName());
}
