package com.megadict.format.dict.index;

import java.io.*;
import java.util.*;
import com.megadict.exception.*;

public class DirectByteBufferedSegnmentBuilder extends BaseSegmentBuilder implements SegmentBuilder {

    public DirectByteBufferedSegnmentBuilder(File indexFile) {
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
        while (reader.read(inputBuffer) != -1) {
            cleanUpBufferContent();
            createAndSaveSegmentToFile();
        }
    }

    private void cleanUpBufferContent() {
        cleanLeftOverIfAny();
        appendPreviousLeftOverIfAny();
        updateLeftOverForNextRead();
    }

    private void cleanLeftOverIfAny() {
        currentLeftOver = BufferTool.extractBufferLeftOver(inputBuffer);
        outputBuffer = (currentLeftOver.length == 0) ? inputBuffer : cleanLeftOver(inputBuffer, currentLeftOver);
    }

    private byte[] cleanLeftOver(byte[] inputBuffer, byte[] leftOver) {
        resetBuffer(outputBuffer);
        return BufferTool.copyBackwardWithOffset(inputBuffer, leftOver.length, outputBuffer);
    }
    
    private void resetBuffer(byte[] buffer) {
        Arrays.fill(buffer, (byte) 0);
    }

    private void appendPreviousLeftOverIfAny() {
        outputBuffer = (previousLeftOver.length == 0) ? outputBuffer : appendPreviousLeftOver(outputBuffer);
    }

    private byte[] appendPreviousLeftOver(byte[] previousBuffer) {
        startPositionToWrite = determineSegmentContentOffset();
        int usedSpaceInOutputBuffer = inputBuffer.length - currentLeftOver.length;
        return BufferTool.copyBackwardWithDestOffset(previousLeftOver, outputBuffer, usedSpaceInOutputBuffer);
    }

    private void updateLeftOverForNextRead() {
        previousLeftOver = currentLeftOver;
        currentLeftOver = EMPTY_BUFFER;
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
        
        return BufferTool.firstHeadWordIn(inputBuffer);
    }

    private String lastWordInBlock() {
        return BufferTool.lastHeadWordIn(outputBuffer);
    }

    private File makeCurrentSegmentFile() {
        return new File(determineCurrentSegmentPath());
    }

    private void saveSegmentToFile(Segment segment) {
        new ByteArraySegmentContentWriter().write(segment, outputBuffer, startPositionToWrite);
    }
    
    private int determineSegmentContentOffset() {
        int lengthBeforeAppendPreviousLeftOver = inputBuffer.length - currentLeftOver.length;
        int lengthAfterAppend = lengthBeforeAppendPreviousLeftOver + previousLeftOver.length;
        int startPosition = outputBuffer.length - lengthAfterAppend;
        return startPosition;
    }

    private static final byte[] EMPTY_BUFFER = new byte[0];
    private byte[] inputBuffer = new byte[BUFFER_SIZE];
    private byte[] outputBuffer = new byte[BUFFER_SIZE + 500];
    private byte[] previousLeftOver = EMPTY_BUFFER;
    private byte[] currentLeftOver = EMPTY_BUFFER;
    private int startPositionToWrite = 0;
}
