package com.megadict.format.dict.index.segment;

import static com.megadict.format.dict.index.segment.ByteBufferTool.*;

import java.io.*;
import java.util.*;

import com.megadict.exception.*;
import com.megadict.format.dict.util.FileUtil;

class ByteBufferedSegmentBuilder extends BaseSegmentBuilder implements SegmentBuilder {
    
    private static final int BUFFER_SIZE_IN_BYTES = FileUtil.DEFAULT_BUFFER_SIZE_IN_BYTES;
    private static final byte[] coreBuffer = new byte[BUFFER_SIZE_IN_BYTES];

    private Block previousBlock;
    private Block currentBlock;

    private int totalCharsRead = 0;
    private int readCharsThisTime = 0;
    
    public ByteBufferedSegmentBuilder(File indexFile) {
        super(indexFile);
    }

    @Override
    public void build() {

        InputStream reader = null;
        try {
            reader = makeReader();
            synchronized (coreBuffer) {
                performIndexingFileIntoSegments(reader);
            }            
        } catch (FileNotFoundException fnf) {
            throw new ResourceMissingException(indexFile());
        } catch (IOException ioe) {
            throw new OperationFailedException("reading index file", ioe);
        } finally {
            closeReader(reader);
        }
    }

    private InputStream makeReader() throws FileNotFoundException {
        return FileUtil.newRawInputStream(indexFile());
    }

    private void closeReader(InputStream reader) {
        FileUtil.closeInputStream(reader);
    }

    private void performIndexingFileIntoSegments(InputStream reader) throws IOException {
        
        while (stillReceiveDataFrom(reader)) {
            
            if (firstBytesWasRead()) {
                createFirstPaddingBlock();
                continue;
            } else if (finalBytesWasRead()) {
                removeRedundantValuesBeforeProcess();
            }

            currentBlock = determineCurrentBlock();
            createAndStoreSegment();
            updatePreviousBlock(currentBlock);
        }

        processFinalBlock();
    }

    private boolean stillReceiveDataFrom(InputStream reader) throws IOException {
        readCharsThisTime = reader.read(coreBuffer);
        return (readCharsThisTime == -1) ? false : countTotalBytesReadAndContinue();
    }

    private boolean countTotalBytesReadAndContinue() {
        totalCharsRead += readCharsThisTime;
        return true;
    }

    private boolean firstBytesWasRead() {
        return (totalCharsRead == readCharsThisTime);
    }

    private void createFirstPaddingBlock() {
        String headword = extractHeadWordOfPaddingBlock();
        int offset = 0;
        Block block = new Block(headword, offset);
        updatePreviousBlock(block);
    }

    private String extractHeadWordOfPaddingBlock() {
        int firstNewlineChar = findFirstNewlineChar(coreBuffer);
        int firstTabChar = findForwardFirstCharInRange(coreBuffer, 0, firstNewlineChar, '\t');
        byte[] headword = copyOfRange(coreBuffer, 0, firstTabChar);
        return new String(headword);
    }

    private boolean finalBytesWasRead() {
        return readCharsThisTime < coreBuffer.length;
    }

    private void removeRedundantValuesBeforeProcess() {
        int startPositionToWipeOut = determineStartPositionToWipeOutRedundantValues();
        Arrays.fill(coreBuffer, startPositionToWipeOut, coreBuffer.length, (byte) 0);
    }
    
    private int determineStartPositionToWipeOutRedundantValues() {
        int positionOfLastValue = readCharsThisTime - 1;
        return isNewlineChar(positionOfLastValue) ? positionOfLastValue - 1 : positionOfLastValue + 1;
    }
    
    private boolean isNewlineChar(int position) {
        return coreBuffer[position] == (byte) '\n';
    }

    private Block determineCurrentBlock() {
        String headword = extractBlockHeadingWord();        
        int offset = totalCharsRead - readCharsThisTime;        
        return new Block(headword, offset);
    }

    private String extractBlockHeadingWord() {
        int firstNewlineChar = findFirstNewlineChar(coreBuffer);

        int nextTabChar = findForwardFirstCharInRange(coreBuffer, firstNewlineChar,
                coreBuffer.length, '\t');

        byte[] headword = copyOfRange(coreBuffer, firstNewlineChar + 1, nextTabChar);

        return new String(headword);
    }

    private void createAndStoreSegment() {
        Segment newSegment = createSegmentWithBuiltBlock();
        storeCreatedSegment(newSegment);
    }

    private Segment createSegmentWithBuiltBlock() {
        String lowerbound = previousBlock.headword;
        String upperbound = currentBlock.headword;
        int offset = previousBlock.offset;
        int length = currentBlock.offset - previousBlock.offset;

        return new Segment(lowerbound, upperbound, offset, length);
    }

    private void updatePreviousBlock(Block currentBlock) {
        previousBlock = currentBlock;
    }

    private void processFinalBlock() {
        readCharsThisTime = determineLastByteRead();
        currentBlock = createTrailingBlock();
        createAndStoreSegment();
    }

    private Block createTrailingBlock() {
        String headword = extractLastWordOfFinalBlock();
        int offset = totalCharsRead;
        return new Block(headword, offset);
    }

    private String extractLastWordOfFinalBlock() {
        int lastNewlineChar = findLastNewlineCharOfBlock();
        int nextTabChar = findNextTabChar(lastNewlineChar);

        byte[] headword = copyContent(lastNewlineChar + 1, nextTabChar);

        return new String(headword);
    }

    private int findLastNewlineCharOfBlock() {
        int start = readCharsThisTime - 1;
        int end = 0;
        return findBackwardFirstCharInRange(coreBuffer, start, end, '\n');
    }

    private int determineLastByteRead() {
        return totalCharsRead % BUFFER_SIZE_IN_BYTES;
    }

    private int findNextTabChar(int lastNewlineChar) {
        int start = lastNewlineChar;
        int end = coreBuffer.length;
        return findForwardFirstCharInRange(coreBuffer, start, end, '\t');
    }

    private byte[] copyContent(int start, int end) {
        return copyOfRange(coreBuffer, start, end);
    }
}
