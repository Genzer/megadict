package com.megadict.format.dict.index.segment;

import java.util.Arrays;

public class ByteArrayInnerBuffer {

    public ByteArrayInnerBuffer(int bufferSize) {
        inputBuffer = new byte[bufferSize];
        int outputBufferSize = determineOutputBufferSize(bufferSize);
        outputBuffer = new byte[outputBufferSize];
    }

    private int determineOutputBufferSize(int inputBufferSize) {
        return incrementByPercent(inputBufferSize, OUTPUT_BUFFER_SIZE_INCREMENT_FACTOR);
    }
    
    private static int incrementByPercent(int inputBufferSize, double percent) {
        return (int) Math.abs(inputBufferSize * percent);
    }

    public int startPositionToWrite() {
        return startPositionToWrite;
    }

    public void clean() {
        cleanLeftOverIfAny();
        appendPreviousLeftOverIfAny();
        computeOffsetToPrepareWriting();
        updateLeftOverForNextRead();
    }

    void cleanLeftOverIfAny() {
        currentLeftOver = ByteBufferTool.extractBufferLeftOver(inputBuffer);
        cleanOutputBuffer();
        cleanLeftOver();
    }
    
    private void cleanOutputBuffer() {
        int outputPadding = outputBuffer.length - inputBuffer.length;
        int lengthOfLeftOvers = previousLeftOver.length + currentLeftOver.length;
        int lengthWillBeSweptOff = outputPadding + lengthOfLeftOvers;
        Arrays.fill(outputBuffer, 0, lengthWillBeSweptOff, (byte) 0);
    }

    private void cleanLeftOver() {
        int discardLastNewlineChar = determineLeftOverLengthIncludingLastNewline();
        ByteBufferTool.copyBackwardFromSourceOffset(inputBuffer, discardLastNewlineChar, outputBuffer);
    }
    
    private int determineLeftOverLengthIncludingLastNewline() {
        int discardLastNewlineChar = currentLeftOver.length + 1;
        return discardLastNewlineChar;
    }

    void appendPreviousLeftOverIfAny() {
        if (previousLeftOver.length > 0) {
            appendPreviousLeftOver();
        }
    }

    private void appendPreviousLeftOver() {
        int discardedLastNewlineChar = 1;
        int usedSpaceInOutputBuffer = inputBuffer.length - currentLeftOver.length - discardedLastNewlineChar;
        ByteBufferTool.copyBackwardToDestOffset(previousLeftOver, outputBuffer, usedSpaceInOutputBuffer);
    }

    void computeOffsetToPrepareWriting() {
        startPositionToWrite = determineSegmentContentOffset();
    }

    private int determineSegmentContentOffset() {
        int discardedNewlineChar = determineLeftOverLengthIncludingLastNewline();
        int lengthBeforeAppendPreviousLeftOver = inputBuffer.length - discardedNewlineChar;
        int lengthAfterAppend = lengthBeforeAppendPreviousLeftOver + previousLeftOver.length;
        int startPosition = outputBuffer.length - lengthAfterAppend;
        return startPosition;
    }

    private void updateLeftOverForNextRead() {
        previousLeftOver = currentLeftOver;
        currentLeftOver = EMPTY_BUFFER;
    }

    public String fistWord() {
        return ByteBufferTool.firstHeadWordIn(outputBuffer);
    }

    public String lastWord() {
        return ByteBufferTool.lastHeadWordIn(outputBuffer);
    }

    private static final double OUTPUT_BUFFER_SIZE_INCREMENT_FACTOR = 1.02;
    private static final byte[] EMPTY_BUFFER = new byte[0];
    byte[] inputBuffer;
    byte[] outputBuffer;
    byte[] previousLeftOver = EMPTY_BUFFER;
    byte[] currentLeftOver = EMPTY_BUFFER;
    private int startPositionToWrite = 0;
}
