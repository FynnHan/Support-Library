package com.libs.brut.androlib.res.util;

import java.io.File;
import java.net.URI;

import J.dir.Directory;
import J.dir.DirectoryException;
import J.dir.FileDirectory;
import J.dir.ZipRODirectory;

public class ExtFile extends File {

    public ExtFile(File file) {
        super(file.getPath());
    }

    public ExtFile(URI uri) {
        super(uri);
    }

    public ExtFile(File parent, String child) {
        super(parent, child);
    }

    public ExtFile(String parent, String child) {
        super(parent, child);
    }

    public ExtFile(String pathname) {
        super(pathname);
    }

    public Directory getDirectory() throws DirectoryException {
        if (mDirectory == null) {
            if (isDirectory()) {
                mDirectory = new FileDirectory(this);
            } else {
                mDirectory = new ZipRODirectory(this);
            }
        }
        return mDirectory;
    }

    private Directory mDirectory;
}
