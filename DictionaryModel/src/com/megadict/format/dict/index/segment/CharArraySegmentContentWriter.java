package com.megadict.format.dict.index.segment;

import java.io.*;

import com.megadict.exception.OperationFailedException;
import com.megadict.exception.ResourceMissingException;

public class CharArraySegmentContentWriter {
    
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
        FileWriter rawWriter = new FileWriter(segmentFile);
        writer = new BufferedWriter(rawWriter, BUFFER_SIZE_IN_BYTES);
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
    BufferedWriter writer;
    
    
}
