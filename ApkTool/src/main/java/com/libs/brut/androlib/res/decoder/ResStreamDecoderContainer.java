package com.libs.brut.androlib.res.decoder;

import com.libs.brut.androlib.AndrolibException;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class ResStreamDecoderContainer {
    private final Map<String, ResStreamDecoder> mDecoders = new HashMap<String, ResStreamDecoder>();

    public void decode(InputStream in, OutputStream out, String decoderName) throws AndrolibException {
        getDecoder(decoderName).decode(in, out);
    }

    public ResStreamDecoder getDecoder(String name) throws AndrolibException {
        ResStreamDecoder decoder = mDecoders.get(name);
        if (decoder == null) {
            throw new AndrolibException("Undefined decoder: " + name);
        }
        return decoder;
    }

    public void setDecoder(String name, ResStreamDecoder decoder) {
        mDecoders.put(name, decoder);
    }
}
