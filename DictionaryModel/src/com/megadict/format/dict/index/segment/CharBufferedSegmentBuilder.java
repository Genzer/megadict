package com.megadict.format.dict.index.segment;

import java.io.*;
import java.util.*;

import com.megadict.exception.OperationFailedException;
import com.megadict.exception.ResourceMissingException;

public class CharBufferedSegmentBuilder extends BaseSegmentBuilder implements SegmentBuilder {

    public CharBufferedSegmentBuilder(File indexFile) {
        super(indexFile);
    }

    @Override
    public List<Segment> builtSegments() {
        return super.getCreatedSegment();
    }

    @Override
    public void build() {

        FileReader reader = null;
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

    private FileReader makeReader() throws FileNotFoundException {
        return new FileReader(indexFile());
    }

    private void closeReader(FileReader reader) {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException ioe) {
            throw new OperationFailedException("closing data input stream", ioe);
        }
    }

    private void readIndexFileAndSplitIt(FileReader reader) throws IOException {
        while (stillReceiveDataFrom(reader)) {
            cleanUpBufferContent();
            createAndSaveSegmentToFile();
        }
    }

    private boolean stillReceiveDataFrom(FileReader reader) throws IOException {
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
        return buffer.fistWord();
    }

    private String lastWordInBlock() {
        return buffer.lastWord();
    }

    private File makeCurrentSegmentFile() {
        return new File(determineCurrentSegmentPath());
    }

    private void saveSegmentToFile(Segment segment) {
        segmentWriter.write(segment, buffer.outputBuffer(), buffer.startPositionToWrite());
    }

    private CharArrayInnerBuffer buffer = new CharArrayInnerBuffer(BUFFER_SIZE_IN_BYTES);
    private CharArraySegmentContentWriter segmentWriter = new CharArraySegmentContentWriter(true);
}
