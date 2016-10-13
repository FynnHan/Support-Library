package com.libs.brut.androlib.res.decoder;

import com.libs.brut.androlib.AndrolibException;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ResRawStreamDecoder implements ResStreamDecoder {
    @Override
    public void decode(InputStream in, OutputStream out) throws AndrolibException {
        try {
            IOUtils.copy(in, out);
        } catch (IOException ex) {
            throw new AndrolibException("Could not decode raw stream", ex);
        }
    }
}
