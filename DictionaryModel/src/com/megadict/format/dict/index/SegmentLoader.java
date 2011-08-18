package com.megadict.format.dict.index;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SegmentLoader {

    public SegmentLoader(IndexFile indexFile) {
        this.indexFile = indexFile;
        loadedIndexes = new HashSet<Index>();
    }

    private void determineBufferSize() {
        int fileSize = (int) indexFile.asRawFile().length();
        bufferSize = (fileSize <= BUFFER_SIZE_IN_BYTES) ? fileSize : BUFFER_SIZE_IN_BYTES;
    }

    public Set<Index> load() throws IOException {
        makeBuffer();

        InputStream reader = makeReader();

        while (reader.read(buffer) != -1) {
            extractIndexFromBuffer();
        }

        return Collections.emptySet();
    }

    private InputStream makeReader() throws FileNotFoundException {
        return new FileInputStream(indexFile.asRawFile());
    }

    private void makeBuffer() {
        determineBufferSize();
        buffer = new byte[bufferSize];
    }
    
    private void extractIndexFromBuffer() {
        
    }

    private static final int BUFFER_SIZE_IN_BYTES = 18 * 1024;
    private int bufferSize;
    private byte[] buffer;
    private Set<Index> loadedIndexes;
    private final IndexFile indexFile;
}
