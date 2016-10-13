package J.util;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import J.common.BrutException;

abstract public class Jar {
    private final static Set<String> mLoaded = new HashSet<String>();
    private final static Map<String, File> mExtracted =
            new HashMap<String, File>();

    public static File getResourceAsFile(String name) throws BrutException {
        File file = mExtracted.get(name);
        if (file == null) {
            file = extractToTmp(name);
            mExtracted.put(name, file);
        }
        return file;
    }

    public static void load(String libPath) {
        if (mLoaded.contains(libPath)) {
            return;
        }

        File libFile;
        try {
            libFile = getResourceAsFile(libPath);
        } catch (BrutException ex) {
            throw new UnsatisfiedLinkError(ex.getMessage());
        }

        System.load(libFile.getAbsolutePath());
    }

    public static File extractToTmp(String resourcePath) throws BrutException {
        return extractToTmp(resourcePath, "brut_util_Jar_");
    }

    public static File extractToTmp(String resourcePath, String tmpPrefix) throws BrutException {
        try {
            InputStream in = Class.class.getResourceAsStream(resourcePath);
            if (in == null) {
                throw new FileNotFoundException(resourcePath);
            }
            File fileOut = File.createTempFile(tmpPrefix, null);
            fileOut.deleteOnExit();
            OutputStream out = new FileOutputStream(fileOut);
            IOUtils.copy(in, out);
            in.close();
            out.close();
            return fileOut;
        } catch (IOException ex) {
            throw new BrutException("Could not extract resource: " + resourcePath, ex);
        }
    }
}
