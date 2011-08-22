package com.megadict.format.dict.reader;

import java.io.*;

class RandomDictFileReader extends BaseDictFileReader implements DictFileReader {

    private RandomAccessFile fileReader;

    public RandomDictFileReader(File dictFile) {
        super(dictFile);
    }

    @Override
    protected void openStream() throws FileNotFoundException {
        fileReader = new RandomAccessFile(dictFile, "r");
    }

    @Override
    protected void jumpTo(int offset) throws IOException {
        fileReader.seek(offset);
    }

    @Override
    protected byte[] readWithAmountOf(int length) throws IOException {
        byte[] readBytes = new byte[length];
        fileReader.read(readBytes, 0, length);
        return readBytes;
    }

    @Override
    protected void closeReader() throws IOException {
        fileReader.close();
    }
}