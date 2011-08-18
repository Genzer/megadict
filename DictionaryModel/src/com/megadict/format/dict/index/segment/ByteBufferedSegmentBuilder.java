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
        return super.getCreatedSegment();
    }

    @Override
    public void build() {

        InputStream reader = null;
        try {
            reader = makeReader();
            readIndexFileAndSplitIt(reader);
        } catch (FileNotFoundException fnf) {
            throw new ResourceMissingException(indexFile());
        } catch (IOException ioe) {
            throw new OperationFailedException("reading index file", ioe);
        } finally {
            closeReader(reader);
        }
    }

    private InputStream makeReader() throws FileNotFoundException {
        return new FileInputStream(indexFile());
    }

    private void closeReader(InputStream reader) {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException ioe) {
            throw new OperationFailedException("closing data input stream", ioe);
        }
    }

    private void readIndexFileAndSplitIt(InputStream reader) throws IOException {
        while (stillReceiveDataFrom(reader)) {
            if (finalBytesWasRead()) {
                appendTrailingNewlineCharBeforeProcess();
            }
            cleanUpBufferContent();
            createAndSaveSegmentToFile();
            Arrays.fill(buffer.inputBuffer, (byte) 0);
        }
    }

    private boolean stillReceiveDataFrom(InputStream reader) throws IOException {
        readBytes = reader.read(buffer.inputBuffer);
        return readBytes != -1;
    }

    private boolean finalBytesWasRead() {
        return readBytes < buffer.inputBuffer.length;
    }

    private void appendTrailingNewlineCharBeforeProcess() {
        buffer.inputBuffer[readBytes] = '\n';
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
        return buffer.fistWord();
    }

    private String lastWordInBlock() {
        return buffer.lastWord();
    }

    private File makeCurrentSegmentFile() {
        return new File(determineCurrentSegmentPath());
    }

    private void saveSegmentToFile(Segment segment) {
        segmentWriter.write(segment, buffer.outputBuffer, buffer.startPositionToWrite());
    }

    private ByteArrayBufferForSegmentBuilding buffer = new ByteArrayBufferForSegmentBuilding(BUFFER_SIZE_IN_BYTES);
    private ByteArraySegmentContentWriter segmentWriter = new ByteArraySegmentContentWriter();
    private int readBytes = 0;
}
