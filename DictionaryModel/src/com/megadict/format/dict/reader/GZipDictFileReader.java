package com.megadict.format.dict.reader;

import java.io.IOException;
import java.util.zip.GZIPInputStream;

import com.megadict.exception.OperationFailedException;

class GZipDictFileReader implements DictFileReader {

    public GZipDictFileReader(GZIPInputStream inputStream) {
        this.gzipReader = inputStream;
    }
    
    @Override
    public void open() {
        // TODO Implement opening a GZIPInputStream        
    }

    @Override
    public byte[] read(int offset, int length) {
        // TODO Forward invocation to GZIPInputStream read method
        return null;
    }

    @Override
    public void close() {
        try {
            gzipReader.close();
        } catch (IOException ioe) {
            throw new OperationFailedException("closing reader", ioe);
        }
    }

    private final GZIPInputStream gzipReader;
}
