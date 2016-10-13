package J.util;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class BrutIO {
    public static void copyAndClose(InputStream in, OutputStream out)
            throws IOException {
        try {
            IOUtils.copy(in, out);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }

    public static long recursiveModifiedTime(File[] files) {
        long modified = 0;
        for (int i = 0; i < files.length; i++) {
            long submodified = recursiveModifiedTime(files[i]);
            if (submodified > modified) {
                modified = submodified;
            }
        }
        return modified;
    }

    public static long recursiveModifiedTime(File file) {
        long modified = file.lastModified();
        if (file.isDirectory()) {
            File[] subfiles = file.listFiles();
            for (int i = 0; i < subfiles.length; i++) {
                long submodified = recursiveModifiedTime(subfiles[i]);
                if (submodified > modified) {
                    modified = submodified;
                }
            }
        }
        return modified;
    }

    public static CRC32 calculateCrc(InputStream input) throws IOException {
        CRC32 crc = new CRC32();
        int bytesRead;
        byte[] buffer = new byte[8192];
        while((bytesRead = input.read(buffer)) != -1) {
            crc.update(buffer, 0, bytesRead);
        }
        return crc;
    }

    public static void copy(File inputFile, ZipOutputStream outputFile) throws IOException {
        try (
                FileInputStream fis = new FileInputStream(inputFile)
        ) {
            IOUtils.copy(fis, outputFile);
        }
    }

    public static void copy(ZipFile inputFile, ZipOutputStream outputFile, ZipEntry entry) throws IOException {
        try (
                InputStream is = inputFile.getInputStream(entry)
        ) {
            IOUtils.copy(is, outputFile);
        }
    }

}
