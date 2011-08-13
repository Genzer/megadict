package com.megadict.format.dict.index.segment;

import java.util.Arrays;


public class CharArrayInnerBuffer {

    public CharArrayInnerBuffer(int bufferSize) {
        inputBuffer = new char[bufferSize];
        int outputBufferSize = determineOutputBufferSize(bufferSize);
        outputBuffer = new char[outputBufferSize];
    }

    private int determineOutputBufferSize(int inputBufferSize) {
        return incrementByPercent(inputBufferSize, OUTPUT_BUFFER_SIZE_INCREMENT_FACTOR);
    }
    
    private static int incrementByPercent(int inputBufferSize, double percent) {
        return (int) Math.abs(inputBufferSize * percent);
    }

    public char[] inputBuffer() {
        return inputBuffer;
    }

    public char[] outputBuffer() {
        return outputBuffer;
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
        currentLeftOver = CharBufferTool.extractBufferLeftOver(inputBuffer);
        cleanOutputBuffer();
        cleanLeftOver();
    }
    
    private void cleanOutputBuffer() {
        int outputPadding = outputBuffer.length - inputBuffer.length;
        int lengthOfLeftOvers = previousLeftOver.length + currentLeftOver.length;
        int lengthWillBeSweptOff = outputPadding + lengthOfLeftOvers;
        Arrays.fill(outputBuffer, 0, lengthWillBeSweptOff, '\0');
    }

    private void cleanLeftOver() {
        int discardLastNewlineChar = determineLeftOverLengthIncludingLastNewline();
        CharBufferTool.copyBackwardFromSourceOffset(inputBuffer, discardLastNewlineChar, outputBuffer);
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
        CharBufferTool.copyBackwardToDestOffset(previousLeftOver, outputBuffer, usedSpaceInOutputBuffer);
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
    }

    public String fistWord() {
        return CharBufferTool.firstHeadWordIn(outputBuffer);
    }

    public String lastWord() {
        return CharBufferTool.lastHeadWordIn(outputBuffer);
    }

    private static final double OUTPUT_BUFFER_SIZE_INCREMENT_FACTOR = 1.02;
    private static final char[] EMPTY_BUFFER = new char[0];
    private char[] inputBuffer;
    private char[] outputBuffer;
    char[] previousLeftOver = EMPTY_BUFFER;
    char[] currentLeftOver = EMPTY_BUFFER;
    private int startPositionToWrite = 0;
}
