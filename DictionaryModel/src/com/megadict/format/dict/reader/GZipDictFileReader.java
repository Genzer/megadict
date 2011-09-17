package com.megadict.format.dict.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

class GZipDictFileReader extends BaseDictFileReader implements DictFileReader {

    private GZIPInputStream reader;

    public GZipDictFileReader(File gzipFile) {
        super(gzipFile);
    }

    @Override
    protected void openStream() throws FileNotFoundException, IOException {
        FileInputStream fis = new FileInputStream(dictFile);
        reader = new GZIPInputStream(fis);
    }

    @Override
    protected void jumpTo(int offset) throws IOException {
        reader.skip(offset);
    }

    @Override
    protected byte[] readWithAmountOf(int length) throws IOException {
        byte[] readBytes = new byte[length];
        reader.read(readBytes);
        return readBytes;
    }

    @Override
    protected void closeReader() throws IOException {
        reader.close();
    }

}
