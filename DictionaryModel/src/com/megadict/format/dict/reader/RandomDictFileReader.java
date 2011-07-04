package com.megadict.format.dict.reader;

import java.io.*;

class RandomDictFileReader extends DictFileReader {
    
    private RandomAccessFile fileReader;
    
    public RandomDictFileReader(String dictFile) throws FileNotFoundException {
        openFile(dictFile);
    }
    
    private void openFile(String dictFile) throws FileNotFoundException {
        fileReader = new RandomAccessFile(dictFile, "r");
    }
     

    @Override
    public int read(byte[] buffer, int offset, int length)
            throws IOException {
        seekToOffset(offset);
        return fileReader.read(buffer, 0, length);
    }
    
    private void seekToOffset(int offset) throws IOException {
        fileReader.seek(offset);
    }

    @Override
    public void close() throws IOException {
        fileReader.close();  
    }
    
}