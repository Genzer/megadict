package com.megadict.format.dict.reader;

import java.io.*;

class BufferedDictFileReader extends BaseDictFileReader implements DictFileReader {

    public BufferedDictFileReader(File dictFile) {
        super(dictFile);
    }

    @Override
    protected void openStream() throws FileNotFoundException {
        FileInputStream rawStream = new FileInputStream(dictFile);
        fileReader = new BufferedInputStream(rawStream, BUFFER_SIZE);
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
    
    private BufferedInputStream fileReader;
    private static final int BUFFER_SIZE = 8 * 1024;
}
