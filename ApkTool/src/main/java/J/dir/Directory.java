package J.dir;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;

public interface Directory {

    Set<String> getFiles();

    Set<String> getFiles(boolean recursive);

    Map<String, Directory> getDirs();

    Map<String, Directory> getDirs(boolean recursive);

    boolean containsFile(String path);

    boolean containsDir(String path);

    InputStream getFileInput(String path) throws DirectoryException;

    OutputStream getFileOutput(String path) throws DirectoryException;

    Directory getDir(String path) throws PathNotExist;

    Directory createDir(String path) throws DirectoryException;

    boolean removeFile(String path);

    void copyToDir(Directory out) throws DirectoryException;

    void copyToDir(Directory out, String[] fileNames) throws DirectoryException;

    void copyToDir(Directory out, String fileName) throws DirectoryException;

    void copyToDir(File out) throws DirectoryException;

    void copyToDir(File out, String[] fileNames) throws DirectoryException;

    void copyToDir(File out, String fileName) throws DirectoryException;

    int getCompressionLevel(String fileName) throws DirectoryException;

    char separator = '/';
}
