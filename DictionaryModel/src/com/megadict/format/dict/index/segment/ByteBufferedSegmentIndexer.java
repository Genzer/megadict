package com.megadict.format.dict.index.segment;

import static com.megadict.format.dict.index.segment.ByteBufferTool.*;

import java.io.*;
import java.util.*;

import com.megadict.exception.*;
import com.megadict.format.dict.util.FileUtil;

public class ByteBufferedSegmentIndexer extends BaseSegmentBuilder implements SegmentBuilder {

    public ByteBufferedSegmentIndexer(File indexFile) {
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
            reader = makeInputStream();
            synchronized (coreBuffer) {
                performIndexingFileIntoSegments(reader);
            }
        } catch (FileNotFoundException fnf) {
            throw new ResourceMissingException(indexFile());
        } catch (IOException ioe) {
            throw new OperationFailedException("reading index file", ioe);
        } finally {
            closeInputStreamIfNotNull(reader);
        }
    }

    private InputStream makeInputStream() throws FileNotFoundException {
        return FileUtil.newRawInputStream(indexFile());
    }

    private void closeInputStreamIfNotNull(InputStream reader) {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException ioe) {
            throw new OperationFailedException("closing data input stream", ioe);
        }
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
        int footerLeftOverLength = computeFooterLeftOverLength();
        Block block = new Block(0, footerLeftOverLength, headword, 0);
        updatePreviousBlock(block);
    }

    private String extractHeadWordOfPaddingBlock() {
        int firstNewlineChar = findFirstNewlineChar(coreBuffer);
        int firstTabChar = findForwardFirstOccurrenceOfCharInRange(coreBuffer, 0, firstNewlineChar, '\t');
        byte[] headword = copyOfRange(coreBuffer, 0, firstTabChar);
        return new String(headword);
    }

    private boolean finalBytesWasRead() {
        return readCharsThisTime < coreBuffer.length;
    }

    private void removeRedundantValuesBeforeProcess() {
        int startPositionToWipeOut = determineStartPositionToWipeOutRedundantValues();
        byte markingValue = 0;
        Arrays.fill(coreBuffer, startPositionToWipeOut, coreBuffer.length, markingValue);
    }

    private int determineStartPositionToWipeOutRedundantValues() {
        int lastCharOfBlockPos = readCharsThisTime - 1;
        return isNewlineChar(lastCharOfBlockPos) ? lastCharOfBlockPos - 1 : lastCharOfBlockPos;
    }

    private boolean isNewlineChar(int position) {
        return coreBuffer[position] == (byte) '\n';
    }

    private Block determineCurrentBlock() {
        int headerLeftOverLength = computeHeadingLeftOverLength();
        int footerLeftOverLength = computeFooterLeftOverLength();
        String firstWord = extractFirstWord();
        int offset = totalCharsRead - BUFFER_SIZE_IN_BYTES + headerLeftOverLength;
        return new Block(headerLeftOverLength, footerLeftOverLength, firstWord, offset);
    }

    private int computeHeadingLeftOverLength() {
        int firstNewlineCharPos = findFirstNewlineChar(coreBuffer);
        return firstNewlineCharPos + 1;
    }

    private int computeFooterLeftOverLength() {
        int lastNewlineCharPos = findLastNewlineChar(coreBuffer);
        return coreBuffer.length - lastNewlineCharPos;
    }

    private String extractFirstWord() {
        int firstNewlineChar = findFirstNewlineChar(coreBuffer);

        int nextTabChar = findForwardFirstOccurrenceOfCharInRange(coreBuffer, firstNewlineChar,
                coreBuffer.length, '\t');

        byte[] headword = copyOfRange(coreBuffer, firstNewlineChar + 1, nextTabChar);

        return new String(headword);
    }

    private void createAndStoreSegment() {
        Segment newSegment = createAndCountSegment();
        storeCreatedSegment(newSegment);
    }

    private Segment createAndCountSegment() {
        Segment createdSegment = createSegmentWithBuiltBlock();
        return createdSegment;
    }

    private Segment createSegmentWithBuiltBlock() {
        String lowerbound = previousBlock.headword;
        String upperbound = currentBlock.headword;
        int offset = previousBlock.offset;
        int length = computeSegmentLength();

        return new Segment(lowerbound, upperbound, offset, length);
    }

    private int computeSegmentLength() {
        int excludedPreviousHeaderLeftOver = readCharsThisTime - previousBlock.headerLeftOverLength;
        int includedCurrentBlock = excludedPreviousHeaderLeftOver + currentBlock.headerLeftOverLength;
        return includedCurrentBlock;
    }

    private void updatePreviousBlock(Block currentBlock) {
        previousBlock = currentBlock;
    }

    private void processFinalBlock() {
        readCharsThisTime = determineLastByteRead();
        currentBlock = createTrailingBlock();
        createAndStoreSegment();
    }
    
    private int determineLastByteRead() {
        return totalCharsRead % BUFFER_SIZE_IN_BYTES;
    }

    private Block createTrailingBlock() {
        int headerLeftOverLenght = 0;
        int footerLeftOverLength = 0;
        String headword = extractLastWordOfFinalBlock();
        int offset = 0;

        return new Block(headerLeftOverLenght, footerLeftOverLength, headword, offset);
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
        return findBackwardFirstOccurrenceOfCharInRange(coreBuffer, start, end, '\n');
    }

    private int findNextTabChar(int lastNewlineChar) {
        int start = lastNewlineChar;
        int end = coreBuffer.length;
        return findForwardFirstOccurrenceOfCharInRange(coreBuffer, start, end, '\t');
    }

    private byte[] copyContent(int start, int end) {
        return copyOfRange(coreBuffer, start, end);
    }

    private static final byte[] coreBuffer = new byte[BUFFER_SIZE_IN_BYTES];

    private Block previousBlock;
    private Block currentBlock;

    private int totalCharsRead = 0;
    private int readCharsThisTime = 0;
}
