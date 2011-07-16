package com.megadict.format.dict.reader;

public interface DictFileReader {
    void open();
    byte[] read(int offset, int length);
    void close();
}
