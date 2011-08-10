package com.megadict.format.dict.index.segment;

import java.util.Arrays;

public class CharArrayInnerBuffer {

    public CharArrayInnerBuffer(int bufferSize) {
        inputBuffer = new char[bufferSize];
        outputBuffer = new char[bufferSize + 100];
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
        updateLeftOverForNextRead();
    }

    void cleanLeftOverIfAny() {
        currentLeftOver = CharBufferTool.extractBufferLeftOver(inputBuffer);
        outputBuffer = (currentLeftOver.length == 0) ? inputBuffer : cleanLeftOver();
    }

    char[] cleanLeftOver() {
        resetBuffer(outputBuffer);
        int discardLastNewlineChar = currentLeftOver.length + 1;
        return CharBufferTool.copyBackwardFromSourceOffset(inputBuffer, discardLastNewlineChar, outputBuffer);
    }

    private void resetBuffer(char[] buffer) {
        Arrays.fill(buffer, '\0');
    }

    void appendPreviousLeftOverIfAny() {
        outputBuffer = (previousLeftOver.length == 0) ? outputBuffer : appendPreviousLeftOver();
    }

    char[] appendPreviousLeftOver() {
        startPositionToWrite = determineSegmentContentOffset();
        int usedSpaceInOutputBuffer = inputBuffer.length - currentLeftOver.length;
        return CharBufferTool.copyBackwardToDestOffset(previousLeftOver, outputBuffer, usedSpaceInOutputBuffer);
    }

    private void updateLeftOverForNextRead() {
        previousLeftOver = currentLeftOver;
        currentLeftOver = EMPTY_BUFFER;
    }

    int determineSegmentContentOffset() {
        int lengthBeforeAppendPreviousLeftOver = inputBuffer.length - currentLeftOver.length;
        int lengthAfterAppend = lengthBeforeAppendPreviousLeftOver + previousLeftOver.length;
        int startPosition = outputBuffer.length - lengthAfterAppend;
        return startPosition;
    }

    public String fistWord() {
        return CharBufferTool.firstHeadWordIn(inputBuffer);
    }

    public String headWord() {
        return CharBufferTool.lastHeadWordIn(outputBuffer);
    }

    private static final char[] EMPTY_BUFFER = new char[0];
    private char[] inputBuffer;
    private char[] outputBuffer;
    private char[] previousLeftOver = EMPTY_BUFFER;
    private char[] currentLeftOver = EMPTY_BUFFER;
    private int startPositionToWrite = 0;
}
