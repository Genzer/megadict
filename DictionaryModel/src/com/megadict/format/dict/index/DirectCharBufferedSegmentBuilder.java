package com.megadict.format.dict.index;

import java.io.*;
import java.util.*;

import com.megadict.exception.OperationFailedException;
import com.megadict.exception.ResourceMissingException;

public class DirectCharBufferedSegmentBuilder extends BaseSegmentBuilder implements SegmentBuilder {

    public DirectCharBufferedSegmentBuilder(File indexFile) {
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
        currentLeftOver = CharBufferTool.extractBufferLeftOver(inputBuffer);
        outputBuffer = (currentLeftOver.length == 0) ? inputBuffer : cleanLeftOver(inputBuffer, currentLeftOver);
    }

    private char[] cleanLeftOver(char[] inputBuffer, char[] leftOver) {
        resetBuffer(outputBuffer);
        return CharBufferTool.copyBackwardWithOffset(inputBuffer, leftOver.length, outputBuffer);
    }
    
    private void resetBuffer(char[] buffer) {
        Arrays.fill(buffer, '\0');
    }

    private void appendPreviousLeftOverIfAny() {
        outputBuffer = (previousLeftOver.length == 0) ? outputBuffer : appendPreviousLeftOver(outputBuffer);
    }

    private char[] appendPreviousLeftOver(char[] previousBuffer) {
        startPositionToWrite = determineSegmentContentOffset();
        int usedSpaceInOutputBuffer = inputBuffer.length - currentLeftOver.length;
        return CharBufferTool.copyBackwardWithDestOffset(previousLeftOver, outputBuffer, usedSpaceInOutputBuffer);
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
        
        return CharBufferTool.firstHeadWordIn(inputBuffer);
    }

    private String lastWordInBlock() {
        return CharBufferTool.lastHeadWordIn(outputBuffer);
    }

    private File makeCurrentSegmentFile() {
        return new File(determineCurrentSegmentPath());
    }

    private void saveSegmentToFile(Segment segment) {
        new CharArraySegmentContentWriter().write(segment, outputBuffer, startPositionToWrite);
    }
    
    private int determineSegmentContentOffset() {
        int lengthBeforeAppendPreviousLeftOver = inputBuffer.length - currentLeftOver.length;
        int lengthAfterAppend = lengthBeforeAppendPreviousLeftOver + previousLeftOver.length;
        int startPosition = outputBuffer.length - lengthAfterAppend;
        return startPosition;
    }

    private static final char[] EMPTY_BUFFER = new char[0];
    private static final int CHAR_BUFFER_SIZE = BUFFER_SIZE / 2;
    private char[] inputBuffer = new char[CHAR_BUFFER_SIZE];
    private char[] outputBuffer = new char[CHAR_BUFFER_SIZE + 100];
    private char[] previousLeftOver = EMPTY_BUFFER;
    private char[] currentLeftOver = EMPTY_BUFFER;
    private int startPositionToWrite = 0;

}
