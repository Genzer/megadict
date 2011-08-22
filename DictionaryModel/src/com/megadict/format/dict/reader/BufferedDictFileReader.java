package com.megadict.format.dict.reader;

import java.io.*;

import com.megadict.format.dict.util.FileUtil;

class BufferedDictFileReader extends BaseDictFileReader implements DictFileReader {

    private InputStream fileReader;

    public BufferedDictFileReader(File dictFile) {
        super(dictFile);
    }

    @Override
    protected void openStream() throws FileNotFoundException {
        fileReader = FileUtil.newBufferedInputStream(dictFile);
    }

    @Override
    protected void jumpTo(int offset) throws IOException {
        fileReader.skip(offset);
    }

    @Override
    protected byte[] readWithAmountOf(int length) throws IOException {
        byte[] readBytes = new byte[length];
        fileReader.read(readBytes);
        return readBytes;
    }

    @Override
    protected void closeReader() throws IOException {
        fileReader.close();
    }

    @Override
    public String toString() {
        return "BufferedDictReader[file: " + this.dictFile + "]";
    }
}
