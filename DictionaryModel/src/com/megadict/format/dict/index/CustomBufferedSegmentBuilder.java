package com.megadict.format.dict.index;

import java.io.*;
import java.util.*;
import com.megadict.exception.*;

class CustomBufferedSegmentBuilder extends BaseSegmentBuilder implements SegmentBuilder {

    public CustomBufferedSegmentBuilder(File indexFile) {
        super(indexFile);
    }

    @Override
    public List<Segment> builtSegments() {
        return createdSegments;
    }

    @Override
    public void build() {

        DataInputStream reader = null;
        try {
            reader = makeReader();
            while (reader.read(buffer) != -1) {
                processLeftOverFromPreviousReadIfAny();
                createAndSaveSegmentToFile();
            }
        } catch (FileNotFoundException fnf) {
            throw new ResourceMissingException(getIndexFile());
        } catch (IOException ioe) {
            throw new OperationFailedException("reading index file", ioe);
        } finally {
            if (reader != null) {
                closeReader(reader);
            }
        }
    }

    private DataInputStream makeReader() throws FileNotFoundException {
        return new DataInputStream(new FileInputStream(getIndexFile()));
    }
    

    private void processLeftOverFromPreviousReadIfAny() {
        appendPreviousLeftOverIfAny();
        cleanLeftOverIfAny();
    }
    
    private void appendPreviousLeftOverIfAny() {
        buffer = (leftOver.length == 0) ? buffer : BufferTool.concatenate(leftOver, buffer);
    }
    
    private void cleanLeftOverIfAny() {
        leftOver = BufferTool.extractBufferLeftOver(buffer);        
        buffer = (leftOver.length == 0) ? buffer : BufferTool.cleanLeftOver(buffer);
    }

    private void createAndSaveSegmentToFile() {
        Segment newSegment = createAndCountSegment();
        recordCreatedSegment(newSegment);
        saveSegmentToFile(newSegment);
    }

    private Segment createAndCountSegment() {
        countCreatedSegment();
        return createSegmentWithCurrentBuffer();
    }

    private Segment createSegmentWithCurrentBuffer() {
        String lowerbound = firstWordInBlock();
        String upperbound = lastWordInBlock();
        File currentSegmentFile = makeCurrentSegmentFile();
        return new Segment(lowerbound, upperbound, currentSegmentFile);
    }

    private String firstWordInBlock() {
        return BufferTool.firstHeadWordIn(buffer);
    }

    private String lastWordInBlock() {
        return BufferTool.lastHeadWordIn(buffer);
    }

    private File makeCurrentSegmentFile() {
        return new File(computeCurrentSegmentPath());
    }

    private void saveSegmentToFile(Segment segment) {
        DataOutputStream writer = null;
        try {
            writer = new DataOutputStream(new FileOutputStream(segment.file()));
            writer.write(buffer);
            writer.flush();
        } catch (FileNotFoundException fnf) {
            throw new ResourceMissingException(segment.file());
        } catch (IOException ioe) {
            throw new OperationFailedException("writing segment file", ioe);
        } finally {
            closeWriter(writer);
        }
    }

    private void closeReader(DataInputStream reader) {
        try {
            reader.close();
        } catch (IOException ioe) {
            throw new OperationFailedException("closing data input stream", ioe);
        }
    }

    private void closeWriter(DataOutputStream writer) {
        try {
            writer.close();
        } catch (IOException ioe) {
            throw new OperationFailedException("closing writer", ioe);
        }
    }

    private static final byte[] EMPTY_BUFFER = new byte[0];
    private byte[] buffer = new byte[BUFFER_SIZE];
    private byte[] leftOver = EMPTY_BUFFER;
}
