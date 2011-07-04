package com.megadict.format.dict.reader;

import java.io.IOException;
import java.util.zip.GZIPInputStream;

class GZipDictFileReader extends DictFileReader {

    public GZipDictFileReader(GZIPInputStream inputStream) {
        this.gzipReader = inputStream;
    }

    @Override
    public int read(byte[] buffer, int offset, int length) throws IOException {
        // TODO: find an appropriate algorithm to read like randon file access.
        return 0;
    }

    @Override
    public void close() throws IOException {
        gzipReader.close();
    }

    private final GZIPInputStream gzipReader;

}
