package com.megadict.format.dict.reader;

import java.io.IOException;

abstract class DictFileReader {
    public abstract int read(byte[] buffer, int offset, int length) throws IOException;
    public abstract void close() throws IOException;
}
