package com.megadict.format.dict;

import java.io.*;

import com.megadict.exception.OperationFailedException;
import com.megadict.format.dict.index.Index;
import com.megadict.format.dict.index.IndexFile;
import com.megadict.format.dict.index.segment.ByteBufferTool;
import com.megadict.format.dict.parser.*;
import com.megadict.format.dict.util.*;

class FirstEntryReader {
    
    private static final byte[] buffer = new byte[1024];
    private static final IndexParser parser = IndexParsers.newParser();
    
    private final InputStream reader;
    
    public FirstEntryReader(IndexFile indexFile) {
        this.reader = FileUtil.newRawInputStream(indexFile.asRawFile());
    }
    
    public FirstEntryReader(InputStream inputStream) {
        this.reader = inputStream;
    }

    public Index read() {
        synchronized (buffer) {
            String indexString = readFirstIndexString();
            return parser.parse(indexString);
        }
    }

    private String readFirstIndexString() {
        try {
            reader.read(buffer);
            int firstNewlineChar = ByteBufferTool.findFirstNewlineChar(buffer);
            byte[] indexStringInByte = ByteBufferTool.copyOfRange(buffer, 0, firstNewlineChar);
            return new String(indexStringInByte);
        } catch (IOException ioe) {
            throw new OperationFailedException("reading index file", ioe);
        } finally {
            FileUtil.closeInputStream(reader);
        }
    }
}
