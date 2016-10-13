package com.libs.brut.androlib.res.decoder;

import com.libs.brut.androlib.AndrolibException;

import java.io.InputStream;
import java.io.OutputStream;

public interface ResStreamDecoder {
    void decode(InputStream in, OutputStream out) throws AndrolibException;
}
