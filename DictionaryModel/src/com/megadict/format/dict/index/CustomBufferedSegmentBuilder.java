package com.megadict.format.dict.index;

import java.io.*;
import java.util.*;
import com.megadict.exception.*;

public class CustomBufferedSegmentBuilder extends BaseSegmentBuilder implements SegmentBuilder {

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
            while (reader.read(inputBuffer) != -1) {
                processLeftOverFromPreviousReadIfAny();
                createAndSaveSegmentToFile();
            }
        } catch (FileNotFoundException fnf) {
            throw new ResourceMissingException(indexFile());
        } catch (IOException ioe) {
            throw new OperationFailedException("reading index file", ioe);
        } finally {
            if (reader != null) {
                closeReader(reader);
            }
        }
    }

    private DataInputStream makeReader() throws FileNotFoundException {
        return new DataInputStream(new FileInputStream(indexFile()));
    }

    private void processLeftOverFromPreviousReadIfAny() {
        cleanLeftOverIfAny();
        appendPreviousLeftOverIfAny();
        updateLeftOver();
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
        int usedSpaceInOutputBuffer = inputBuffer.length - currentLeftOver.length;
        return BufferTool.copyBackwardWithDestOffset(previousLeftOver, outputBuffer, usedSpaceInOutputBuffer);
    }
    
    private void updateLeftOver() {
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
        return BufferTool.lastHeadWordIn(inputBuffer);
    }

    private File makeCurrentSegmentFile() {
        return new File(determineCurrentSegmentPath());
    }

    private void saveSegmentToFile(Segment segment) {
        BufferedOutputStream writer = null;
        try {
            writer = new BufferedOutputStream(new FileOutputStream(segment.file()));
            writer.write(inputBuffer);
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

    private void closeWriter(BufferedOutputStream writer) {
        try {
            writer.close();
        } catch (IOException ioe) {
            throw new OperationFailedException("closing writer", ioe);
        }
    }

    private static final byte[] EMPTY_BUFFER = new byte[0];
    private byte[] inputBuffer = new byte[BUFFER_SIZE];
    private byte[] outputBuffer = new byte[BUFFER_SIZE + 300];
    private byte[] previousLeftOver = EMPTY_BUFFER;
    private byte[] currentLeftOver = EMPTY_BUFFER;
}
