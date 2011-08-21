package com.megadict.format.dict.index;

import java.io.*;
import java.nio.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import com.megadict.exception.OperationFailedException;
import com.megadict.exception.ResourceMissingException;
import com.megadict.format.dict.index.segment.Segment;
import com.megadict.format.dict.index.segment.SegmentStore;
import com.megadict.format.dict.util.FileUtil;
import com.megadict.format.dict.util.UnicodeDecoder;

class RandomIndexFileReader extends BaseIndexFileReader implements IndexFileReader {

    private static final byte[] byteBuffer = new byte[FileUtil.LARGE_BUFFER_SIZE_IN_BYTES];
    private static final char[] charBuffer = new char[FileUtil.LARGE_BUFFER_SIZE_IN_BYTES / 2];

    private static final ByteBuffer decodingByteBuffer = ByteBuffer.allocate(byteBuffer.length);
    private static final CharBuffer decodingCharBuffer = CharBuffer.allocate(charBuffer.length);

    private final ByteBuffer randomReader;
    private final SegmentStore segmentStore;

    public RandomIndexFileReader(File indexFile, SegmentStore segmentStore) {
        super(indexFile);
        this.segmentStore = segmentStore;
        randomReader = makeByteBuffer(indexFile);
    }

    private static ByteBuffer makeByteBuffer(File file) {
        try {
            RandomAccessFile randomIndex = new RandomAccessFile(file, "r");
            FileChannel fc = randomIndex.getChannel();
            MappedByteBuffer byteBuffer = fc.map(MapMode.READ_ONLY, 0, fc.size());
            return byteBuffer;
        } catch (FileNotFoundException fnf) {
            throw new ResourceMissingException(file);
        } catch (IOException ioe) {
            throw new OperationFailedException("mapping byte buffer with index file", ioe);
        }
    }

    @Override
    protected void makeReader() {
        // Do not need to make a new reader because the reader is created when
        // constructing this class.
    }

    @Override
    protected int locateIndexStringOf(String headword) throws IOException {
        synchronized (byteBuffer) {
            Segment segment = determineContainingSegment(headword);

            randomReader.clear();
            randomReader.position(segment.offset());

            int contentLength = determineContentLength(segment.length());
            randomReader.get(byteBuffer, 0, contentLength);

            decodeToCharArray(byteBuffer, charBuffer);

            builder.append(charBuffer);
            int foundPosition = builder.indexOf(headword);
            return (foundPosition != -1) ? foundPosition : -1;
        }        
    }

    private Segment determineContainingSegment(String headword) {
        String originalHeadword = rollBackToOriginalForm(headword);
        return segmentStore.findSegmentPossiblyContains(originalHeadword);
    }

    private String rollBackToOriginalForm(String headword) {
        return headword.replaceAll("[\n\t]", "");
    }

    private int determineContentLength(int segmentLength) {
        int remaining = randomReader.remaining();
        int candidateBufferSize = segmentLength * 2;
        return Math.min(remaining, candidateBufferSize);
    }

    private void decodeToCharArray(byte[] sourceByteArray, char[] destCharArray) throws IOException {
        updateNewContent(sourceByteArray);
        decode();
        copyContentToArray(destCharArray);
        cleanUp();
    }

    private void updateNewContent(byte[] newContent) {
        decodingByteBuffer.clear();
        decodingByteBuffer.put(newContent);
        decodingByteBuffer.flip();
    }

    private void decode() {
        decodingCharBuffer.clear();
        UnicodeDecoder.decode(decodingByteBuffer, decodingCharBuffer, true);
        decodingCharBuffer.clear();
    }

    private void copyContentToArray(char[] destCharArray) {
        int length = Math.min(destCharArray.length, decodingCharBuffer.capacity());
        decodingCharBuffer.get(destCharArray, 0, length);
    }

    private void cleanUp() {
        decodingByteBuffer.compact();
        decodingCharBuffer.clear();
    }

    @Override
    protected void closeReader() {
        // Do nothing because JVM will garbage collector will automically do the
        // job.
    }
}
