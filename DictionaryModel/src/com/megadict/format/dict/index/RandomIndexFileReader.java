package com.megadict.format.dict.index;

import java.io.*;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.*;

import com.megadict.exception.*;
import com.megadict.format.dict.index.segment.Segment;
import com.megadict.format.dict.parser.*;
import com.megadict.format.dict.util.FileUtil;

public class RandomIndexFileReader {

    static {
        CHAR_BUFFER_SIZE = determineCharBufferSize(FileUtil.DEFAULT_BUFFER_SIZE_IN_BYTES);
        NUM_OF_CHAR_TO_BE_READ_ON = determineNumOfCharShouldBeReadOn(10);
    }

    private static int determineCharBufferSize(int bufferSizeInBytes) {
        int sizeOfCharInBytes = 2;
        return bufferSizeInBytes / sizeOfCharInBytes;
    }

    private static int determineNumOfCharShouldBeReadOn(int numOfWordShouldReadOn) {
        int numOfCharPerWord = 100;
        return numOfWordShouldReadOn * numOfCharPerWord;
    }

    public RandomIndexFileReader(File indexFile) {
        this.indexFile = indexFile;
    }

    public Index getIndexOf(String headword, Segment segment) {
        try {
            makeRandomReader();
            String instrumentedHeadword = createExactMatching(headword);
            int foundPosition = locateIndexStringOf(instrumentedHeadword, segment);
            String indexString = readWholeLineAt(foundPosition);
            return makeNewIndex(indexString);
        } catch (FileNotFoundException fnf) {
            throw new ResourceMissingException(indexFile);
        } catch (IOException ioe) {
            throw new OperationFailedException("reading index file", ioe);
        }
    }

    private void makeRandomReader() throws IOException {
        RandomAccessFile randomIndex = new RandomAccessFile(indexFile, "r");
        FileChannel fc = randomIndex.getChannel();
        MappedByteBuffer byteBuffer = fc.map(MapMode.READ_ONLY, 0, fc.size());
        Charset unicodeCharset = Charset.forName("UTF-8");
        CharsetDecoder decoder = unicodeCharset.newDecoder();
        randomReader = decoder.decode(byteBuffer);
    }

    private static String createExactMatching(String headword) {
        return HEAD_WORD_PREFIX + headword + HEAD_WORD_SUFFIX;
    }

    private int locateIndexStringOf(String headword, Segment segment) throws IOException {
        randomReader.position(segment.offset());
        charBuffer = new char[segment.length()];
        
        randomReader.get(charBuffer);

        builder.append(charBuffer);

        int foundPosition = builder.indexOf(headword);

        return (foundPosition != -1) ? foundPosition : -1;
    }

    private String readWholeLineAt(int startPosition) {
        String[] readLines = readAsManyIndexStringAsPossible(startPosition);
        if (readLines.length > 0) {
            return readLines[0];
        } else {
            return null;
        }
    }

    private String[] readAsManyIndexStringAsPossible(int startPosition) {
        if (startPosition == -1) {
            return EMPTY_STRING_ARRAY;
        }
        // Ignore heading new line character "\n".
        startPosition++;

        int endPosition = startPosition + NUM_OF_CHAR_TO_BE_READ_ON;

        int currentBufferLength = builder.length();
        // TODO: It's still has a bug in case when the found record is only
        // half-read, and we have to continue read. Should use a marker.
        if (endPosition >= currentBufferLength) {
            endPosition = currentBufferLength;
        }

        String rawString = builder.substring(startPosition, endPosition).trim();

        return rawString.split("\n");
    }

    private Index makeNewIndex(String indexString) {
        try {
            return indexParser.parse(indexString);
        } catch (ParseIndexException pie) {
            return null;
        }
    }

    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private static final String HEAD_WORD_PREFIX = "\n";
    private static final String HEAD_WORD_SUFFIX = "\t";

    private static final int CHAR_BUFFER_SIZE;
    private static final int NUM_OF_CHAR_TO_BE_READ_ON;

    private char[] charBuffer;
    private final StringBuilder builder = new StringBuilder(CHAR_BUFFER_SIZE);
    private final IndexParser indexParser = IndexParsers.newInstance();
    // private Reader reader;
    private CharBuffer randomReader;
    private final File indexFile;
}
