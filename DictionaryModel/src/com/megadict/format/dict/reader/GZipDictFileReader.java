package com.megadict.format.dict.reader;

import java.io.IOException;
import java.util.zip.GZIPInputStream;

class GZipDictFileReader extends DictFileReader {

    public GZipDictFileReader(GZIPInputStream inputStream) {
        this.gzipReader = inputStream;
    }

    @Override
    public int read(byte[] buffer, int offset, int length) throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void close() throws IOException {
        // TODO Auto-generated method stub

    }
    
    private final GZIPInputStream gzipReader;

}
