package com.megadict.format.dict.index.segment;

import java.util.Arrays;

public class ByteArrayInnerBuffer {

    public ByteArrayInnerBuffer(int bufferSize) {
        inputBuffer = new byte[bufferSize];
        outputBuffer = new byte[bufferSize + 100];
    }

    public byte[] inputBuffer() {
        return inputBuffer;
    }

    public byte[] outputBuffer() {
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

    private int determineSegmentContentOffset() {
        int lengthBeforeAppendPreviousLeftOver = inputBuffer.length - currentLeftOver.length;
        int lengthAfterAppend = lengthBeforeAppendPreviousLeftOver + previousLeftOver.length;
        int startPosition = outputBuffer.length - lengthAfterAppend;
        return startPosition;
    }
    
    public String firstWord() {        
        return BufferTool.firstHeadWordIn(inputBuffer);
    }

    public String lastWord() {
        return BufferTool.lastHeadWordIn(outputBuffer);
    }

    private static final byte[] EMPTY_BUFFER = new byte[0];
    private byte[] inputBuffer;
    private byte[] outputBuffer;
    private byte[] previousLeftOver = EMPTY_BUFFER;
    private byte[] currentLeftOver = EMPTY_BUFFER;
    private int startPositionToWrite = 0;
}
