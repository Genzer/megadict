package com.megadict.format.dict.index;

import java.io.*;

import com.megadict.exception.OperationFailedException;
import com.megadict.format.dict.index.segment.ByteBufferTool;
import com.megadict.format.dict.parser.*;
import com.megadict.format.dict.util.*;

class FirstEntryReader {

    private FirstEntryReader() {
    }

    public static Index read(IndexFile indexFile) {
        InputStream reader = makeReader(indexFile);
        return performReader(reader);
    }

    public static Index read(InputStream stream) {
        return performReader(stream);
    }

    private static Index performReader(InputStream reader) {
        synchronized (buffer) {
            String indexString = readFirstIndexString(reader);
            return parser.parse(indexString);
        }
    }

    private static String readFirstIndexString(InputStream reader) {
        try {
            reader.read(buffer);
            int firstNewlineChar = ByteBufferTool.findFirstNewlineChar(buffer);
            byte[] indexStringInByte = ByteBufferTool.copyOfRange(buffer, 0, firstNewlineChar);
            return new String(indexStringInByte);
        } catch (IOException ioe) {
            throw new OperationFailedException("reading index file", ioe);
        }
    }

    private static InputStream makeReader(IndexFile indexFile) {
        return FileUtil.newRawInputStream(indexFile.asRawFile());
    }

    private static final byte[] buffer = new byte[1024];
    private static final IndexParser parser = IndexParsers.newParser();
}
