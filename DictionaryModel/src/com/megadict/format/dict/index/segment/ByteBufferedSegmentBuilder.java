package com.megadict.format.dict.index.segment;

import java.io.*;
import java.util.*;
import com.megadict.exception.*;

public class ByteBufferedSegmentBuilder extends BaseSegmentBuilder implements SegmentBuilder {

    public ByteBufferedSegmentBuilder(File indexFile) {
        super(indexFile);
    }

    @Override
    public List<Segment> builtSegments() {
        return getCreatedSegment();
    }

    @Override
    public void build() {

        DataInputStream reader = null;
        try {
            reader = makeReader();
            readIndexFileAndPerformSplitting(reader);
        } catch (FileNotFoundException fnf) {
            throw new ResourceMissingException(indexFile());
        } catch (IOException ioe) {
            throw new OperationFailedException("reading index file", ioe);
        } finally {
            closeReader(reader);
        }
    }

    private DataInputStream makeReader() throws FileNotFoundException {
        return new DataInputStream(new FileInputStream(indexFile()));
    }

    private void closeReader(DataInputStream reader) {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException ioe) {
            throw new OperationFailedException("closing data input stream", ioe);
        }
    }

    private void readIndexFileAndPerformSplitting(DataInputStream reader) throws IOException {
        while (stillReceiveDataFromReader(reader)) {
            cleanUpBufferContent();
            createAndSaveSegmentToFile();
        }
    }

    private boolean stillReceiveDataFromReader(DataInputStream reader) throws IOException {
        return reader.read(buffer.inputBuffer()) != -1;
    }
    
    private void cleanUpBufferContent() {
        buffer.clean();
    }

    private void createAndSaveSegmentToFile() {
        Segment newSegment = createAndCountSegment();
        storeCreatedSegment(newSegment);
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
        return buffer.firstWord();
    }

    private String lastWordInBlock() {
        return buffer.lastWord();
    }

    private File makeCurrentSegmentFile() {
        return new File(determineCurrentSegmentPath());
    }

    private void saveSegmentToFile(Segment segment) {
        new ByteArraySegmentContentWriter().write(segment, buffer.outputBuffer(), buffer.startPositionToWrite());
    }

    private ByteArrayInnerBuffer buffer = new ByteArrayInnerBuffer(BUFFER_SIZE_IN_BYTES);
}
