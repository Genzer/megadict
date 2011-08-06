package com.megadict.format.dict.index;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

import com.megadict.exception.*;
import com.megadict.format.dict.parser.*;

public class IndexFileReader {

    public IndexFileReader(File indexFile) {
        this.indexFile = indexFile;
    }

    public Index getIndexOf(String headword) {
        String indexString = findInFile(headword);
        return (indexString != null) ? makeNewIndex(indexString) : null;
    }

    public Set<Index> getIndexesStartFrom(String headword) {

        Set<Index> indexes = new HashSet<Index>();

        String[] indexStrings = retrieveIndexStringsFrom(headword);

        for (String indexString : indexStrings) {
            Index newIndex = makeNewIndex(indexString);
            if (newIndex != null) {
                indexes.add(newIndex);
            }
        }

        return indexes;
    }

    private Index makeNewIndex(String indexString) {
        try {
            return INDEX_PARSER.parse(indexString);
        } catch (ParseIndexException pie) {
            return null;
        }
    }

    private String findInFile(String headword) {
        try {
            makeReader();
            String customedHeadword = createExactMatching(headword);
            int foundPosition = locateIndexStringOf(customedHeadword);
            return readWholeLineAt(foundPosition);
        } catch (FileNotFoundException fnf) {
            throw new ResourceMissingException(indexFile, fnf);
        } catch (IOException ioException) {
            throw new OperationFailedException("reading index file", ioException);
        } finally {
            closeReader();
        }
    }

    private String[] retrieveIndexStringsFrom(String headword) {
        try {
            makeReader();
            String customedheadword = createFuzzyMatching(headword);
            int foundPosition = locateIndexStringOf(customedheadword);
            return readAsManyIndexStringAsPossible(foundPosition);
        } catch (FileNotFoundException fnf) {
            throw new ResourceMissingException(indexFile, fnf);
        } catch (IOException ioException) {
            throw new OperationFailedException("reading index file", ioException);
        } finally {
            closeReader();
        }
    }

    private void makeReader() throws FileNotFoundException, IOException {
        FileInputStream rawStream = new FileInputStream(indexFile);
        reader = new BufferedReader(newUnicodeStream(rawStream), READER_BUFFER_SIZE_IN_BYTES);
    }

    private InputStreamReader newUnicodeStream(FileInputStream rawStream) {
        return new InputStreamReader(rawStream, UTF8_CHARSET);
    }

    private int locateIndexStringOf(String headword) throws IOException {
        fillCharBufferWithSpaces();

        while (stillAbleToRead()) {
            builder.append(CHAR_BUFFER);

            int foundPosition = builder.indexOf(headword);

            if (foundPosition != -1) {
                return foundPosition;
            } else {
                resetBuilder();
                fillCharBufferWithSpaces();
            }
        }
        return -1;
    }

    private static String createFuzzyMatching(String headword) {
        return HEAD_WORD_PREFIX + headword;
    }

    private static String createExactMatching(String headword) {
        return HEAD_WORD_PREFIX + headword + HEAD_WORD_SUFFIX;
    }

    private void fillCharBufferWithSpaces() {
        Arrays.fill(CHAR_BUFFER, ' ');
    }

    private boolean stillAbleToRead() throws IOException {
        return reader.read(CHAR_BUFFER) != -1;
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

    private void resetBuilder() {
        builder.delete(0, builder.length());
    }

    private void closeReader() {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException ioe) {
            throw new OperationFailedException("closing reading", ioe);
        }
    }

    private static final int READER_BUFFER_SIZE_IN_BYTES = 8 * 1024;
    private static final int SIZE_OF_CHAR_IN_BYTES = 2;
    private static final int CHAR_BUFFER_SIZE = READER_BUFFER_SIZE_IN_BYTES / SIZE_OF_CHAR_IN_BYTES;

    private static final int NUM_OF_CHAR_PER_WORD = 100;
    private static final int NUM_OF_WORDS_SHOULD_BE_READ = 10;
    private static final int NUM_OF_CHAR_TO_BE_READ_ON = NUM_OF_WORDS_SHOULD_BE_READ * NUM_OF_CHAR_PER_WORD;
    
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private static final String HEAD_WORD_PREFIX = "\n";
    private static final String HEAD_WORD_SUFFIX = "\t";
    
    private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

    private final char[] CHAR_BUFFER = new char[CHAR_BUFFER_SIZE];
    private final StringBuilder builder = new StringBuilder(CHAR_BUFFER_SIZE);
    private final IndexParser INDEX_PARSER = IndexParsers.newInstance();
    private BufferedReader reader;
    private final File indexFile;
}
