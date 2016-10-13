package J.dir;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import J.common.BrutException;
import J.util.BrutIO;
import J.util.OS;

public class DirUtil {
    public static void copyToDir(Directory in, Directory out)
            throws DirectoryException {
        for (String fileName : in.getFiles(true)) {
            copyToDir(in, out, fileName);
        }
    }

    public static void copyToDir(Directory in, Directory out, String[] fileNames) throws DirectoryException {
        for (int i = 0; i < fileNames.length; i++) {
            copyToDir(in, out, fileNames[i]);
        }
    }

    public static void copyToDir(Directory in, Directory out, String fileName) throws DirectoryException {
        try {
            if (in.containsDir(fileName)) {
                // TODO: remove before copying
                in.getDir(fileName).copyToDir(out.createDir(fileName));
            } else {
                BrutIO.copyAndClose(in.getFileInput(fileName), out.getFileOutput(fileName));
            }
        } catch (IOException ex) {
            throw new DirectoryException("Error copying file: " + fileName, ex);
        }
    }

    public static void copyToDir(Directory in, File out)
            throws DirectoryException {
        for (String fileName : in.getFiles(true)) {
            copyToDir(in, out, fileName);
        }
    }

    public static void copyToDir(Directory in, File out, String[] fileNames) throws DirectoryException {
        for (int i = 0; i < fileNames.length; i++) {
            copyToDir(in, out, fileNames[i]);
        }
    }

    public static void copyToDir(Directory in, File out, String fileName)
            throws DirectoryException {
        try {
            if (in.containsDir(fileName)) {
                OS.rmdir(new File(out, fileName));
                in.getDir(fileName).copyToDir(new File(out, fileName));
            } else {
                if (fileName.equals("res") && !in.containsFile(fileName)) {
                    return;
                }
                File outFile = new File(out, fileName);
                outFile.getParentFile().mkdirs();
                BrutIO.copyAndClose(in.getFileInput(fileName), new FileOutputStream(outFile));
            }
        } catch (IOException ex) {
            throw new DirectoryException("Error copying file: " + fileName, ex);
        } catch (BrutException ex) {
            throw new DirectoryException("Error copying file: " + fileName, ex);
        }
    }
}
