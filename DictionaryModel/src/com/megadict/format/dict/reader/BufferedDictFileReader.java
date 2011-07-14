package com.megadict.format.dict.reader;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class BufferedDictFileReader extends DictFileReader {

    public BufferedDictFileReader(String dictFile) throws FileNotFoundException {
        openFile(dictFile);
    }
    
    private void openFile(String dictFile) throws FileNotFoundException {
        fileReader = new BufferedInputStream(new FileInputStream(dictFile));
    }
    
    @Override
    public int read(byte[] buffer, int offset, int length) throws IOException {
        skipBytes(offset);
        return fileReader.read(buffer);
    }
    
    private void skipBytes(int offset) throws IOException {
        fileReader.skip(offset);
    }

    @Override
    public void close() throws IOException {
        fileReader.close();
    }
    
    private BufferedInputStream fileReader;

}
