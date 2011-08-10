package com.megadict.format.dict.index;

import java.io.*;

import com.megadict.exception.OperationFailedException;
import com.megadict.exception.ResourceMissingException;

public class ByteArraySegmentContentWriter {

    public void write(Segment segment, byte[] content, int startPosition) {
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
    
    private void makeWriter(File segmentFile) throws FileNotFoundException {
        FileOutputStream rawStream = new FileOutputStream(segmentFile);
        writer = new BufferedOutputStream(rawStream, BUFFER_SIZE_IN_BYTES);
    }
    
    private void writeSegmentContent(byte[] content, int startPosition) throws IOException {
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
    BufferedOutputStream writer;
}
