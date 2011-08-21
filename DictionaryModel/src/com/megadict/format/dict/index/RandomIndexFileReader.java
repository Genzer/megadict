package com.megadict.format.dict.index;

import java.io.*;
import java.nio.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import com.megadict.exception.OperationFailedException;
import com.megadict.exception.ResourceMissingException;
import com.megadict.format.dict.index.segment.Segment;
import com.megadict.format.dict.index.segment.SegmentStore;
import com.megadict.format.dict.util.UnicodeDecoder;

class RandomIndexFileReader extends BaseIndexFileReader implements IndexFileReader {

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

        Segment segment = determineContainingSegment(headword);

        randomReader.clear();
        randomReader.position(segment.offset());

        int bufferSize = determineBufferSize(segment.length());
        byte[] byteBuffer = new byte[bufferSize];
        randomReader.get(byteBuffer);

        char[] charArray = new char[byteBuffer.length];
        decodeToCharArray(byteBuffer, charArray);

        builder.append(charArray);
        int foundPosition = builder.indexOf(headword);
        return (foundPosition != -1) ? foundPosition : -1;
    }

    private Segment determineContainingSegment(String headword) {
        return segmentStore.findSegmentPossiblyContains(headword);
    }

    private int determineBufferSize(int segmentLength) {
        int remaining = randomReader.remaining();
        int candidateBufferSize = segmentLength * 2;
        return Math.max(remaining, candidateBufferSize);
    }

    private void decodeToCharArray(byte[] sourcebyteArray, char[] destCharArray) throws IOException {
        CharBuffer decoded = decode(sourcebyteArray);
        int limit = decoded.limit();
        decoded.get(destCharArray, 0, limit);
    }

    private CharBuffer decode(byte[] content) {
        ByteBuffer wrappedContent = ByteBuffer.wrap(content);
        return UnicodeDecoder.decode(wrappedContent);
    }

    @Override
    protected void closeReader() {
        // Do nothing because JVM will garbage collector will automically do the
        // job.
    }
}
