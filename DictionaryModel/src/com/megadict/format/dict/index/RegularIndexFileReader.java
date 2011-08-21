package com.megadict.format.dict.index;

import java.io.*;

import java.util.Arrays;

import com.megadict.format.dict.util.FileUtil;

class RegularIndexFileReader extends BaseIndexFileReader implements IndexFileReader {
    
    private final char[] charBuffer = new char[CHAR_BUFFER_SIZE];
    private Reader reader;
    
    public RegularIndexFileReader(File indexFile) {
        super(indexFile);
    }

    @Override
    protected void makeReader() {
        reader = FileUtil.newUnicodeBufferedReader(indexFile, FileUtil.LARGE_BUFFER_SIZE_IN_BYTES);
    }

    @Override
    protected int locateIndexStringOf(String headword) throws IOException {
        fillCharBufferWithSpaces();

        while (stillAbleToRead()) {
            builder.append(charBuffer);

            int foundPosition = builder.indexOf(headword);

            if (foundPosition != -1) {
                return foundPosition;
            } else {
                resetBuilder();
                fillCharBufferWithSpaces();
            }
        }
        return -1;
    }
    
    private void fillCharBufferWithSpaces() {
        Arrays.fill(charBuffer, ' ');
    }

    private boolean stillAbleToRead() throws IOException {
        return reader.read(charBuffer) != -1;
    }

    private void resetBuilder() {
        builder.delete(0, builder.length());
    }

    @Override
    protected void closeReader() {
        FileUtil.closeReader(reader);
    }
}
