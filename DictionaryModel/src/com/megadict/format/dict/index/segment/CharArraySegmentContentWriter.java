package com.megadict.format.dict.index.segment;

import java.io.*;

import com.megadict.exception.OperationFailedException;
import com.megadict.exception.ResourceMissingException;

class CharArraySegmentContentWriter {
    
    public CharArraySegmentContentWriter() {
        this.bufferEnabled = false;
    }
    
    public CharArraySegmentContentWriter(boolean bufferEnabled) {
        this.bufferEnabled = bufferEnabled;
    }
    
    public void write(Segment segment, char[] content, int startPosition) {
        try {
            makeWriter(segment.file());
            writeSegmentContent(content, startPosition);
        } catch (FileNotFoundException fnf) {
            throw new ResourceMissingException(segment.file());
        } catch (IOException ioe) {
            throw new OperationFailedException("cannot write segment content", ioe);
        } finally {
            closeWriter();
        }
    }
    
    private void makeWriter(File segmentFile) throws IOException {
        writer = bufferEnabled ? makeBufferedWriter(segmentFile) : makeRegularWriter(segmentFile);
    }
    
    private Writer makeBufferedWriter(File segmentFile) throws IOException {
        return new BufferedWriter(makeRegularWriter(segmentFile), BUFFER_SIZE_IN_BYTES);
    }
    
    private Writer makeRegularWriter(File segmentFile) throws IOException {
        return new FileWriter(segmentFile);
    }
    
    private void writeSegmentContent(char[] content, int startPosition) throws IOException {
        int lengthToWrite = content.length - startPosition;
        writer.write(content, startPosition, lengthToWrite);
        writer.flush();
    }
    
    private void closeWriter() {
        try {
            writer.close();
        } catch (IOException ioe) {
            throw new OperationFailedException("closing segment writer", ioe);
        }
    }
    
    private static final int BUFFER_SIZE_IN_BYTES = 8 * 1024;
    private final boolean bufferEnabled;
    Writer writer;   
}
