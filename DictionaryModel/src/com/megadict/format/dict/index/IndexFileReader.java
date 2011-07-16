package com.megadict.format.dict.index;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;

import com.megadict.exception.OperationFailedException;
import com.megadict.exception.ResourceMissingException;
import com.megadict.format.dict.parser.IndexParser;
import com.megadict.format.dict.parser.IndexTabDilimeterParser;

class IndexFileReader {

    public IndexFileReader(File indexFile) {
        this.indexFile = indexFile;
    }

    public Index getIndexOf(String headWord) {
        String indexString = findInFile(headWord);
        if (indexString != null) {
            return INDEX_PARSER.parse(indexString);
        } else {
            return null;
        }
    }

    public String getIndexStringOf(String word) {
        return findInFile(word);
    }

    private String findInFile(String headWord) {
        try {
            makeReader();
            String found = locateIndexStringOf(headWord);
            return found;
        } catch (FileNotFoundException fnf) {
            throw new ResourceMissingException(indexFile, fnf);
        } catch (IOException ioException) {
            throw new OperationFailedException("reading index file",
                    ioException);
        } finally {
            closeReader();
        }
    }

    private void makeReader() throws FileNotFoundException {
        FileInputStream rawStream = new FileInputStream(indexFile);
        reader = new BufferedReader(newUnicodeStream(rawStream),
                INNER_READER_BUFFER_SIZE);
    }

    private InputStreamReader newUnicodeStream(FileInputStream rawStream) {
        return new InputStreamReader(rawStream, UTF8_CHARSET);
    }

    private String locateIndexStringOf(String headWord) throws IOException {
        headWord = matchingWholeWord(headWord);
        fillCharBufferWithSpaces();

        while (stillAbleToRead()) {
            builder.append(CHAR_BUFFER);

            int foundPosition = builder.indexOf(headWord);

            if (foundPosition != -1) {
                return readWholeLineAt(foundPosition);
            } else {
                resetBuilder();
            }
        }

        return null;
    }

    private static String matchingWholeWord(String headWord) {
        return HEAD_WORD_PREFIX + headWord + HEAD_WORD_SUFFIX;
    }

    private void fillCharBufferWithSpaces() {
        Arrays.fill(CHAR_BUFFER, ' ');
    }

    private boolean stillAbleToRead() throws IOException {
        return reader.read(CHAR_BUFFER) != -1;
    }

    private String readWholeLineAt(int startPosition) {
        // Ignore heading new line character "\n".
        startPosition++;

        // Choose 100 because lines are often no longer than 100 characters.
        int endPosition = startPosition + 100;

        int currentBufferLength = builder.length();
        // TODO: It's still has a bug in case when the found record is only
        // half-read, and we have to continue read. Should use a marker.
        if (endPosition >= currentBufferLength) {
            endPosition = currentBufferLength;
        }

        String rawString = builder.substring(startPosition, endPosition).trim();

        String[] rawStringSplitted = rawString.split("\n");

        return rawStringSplitted[0];
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

    /*
     * Set the size of BufferedReader inner buffer to 8KB according to Android
     * recommendation.
     */
    private static final int INNER_READER_BUFFER_SIZE = 8 * 1024;

    /*
     * Every char in java consumes 2 bytes (16-bit). The number of characters in
     * CHAR_BUFFER is determined from BufferedReader buffer size;
     */
    private static final int CHAR_BUFFER_SIZE = INNER_READER_BUFFER_SIZE / 2;

    private static final String HEAD_WORD_PREFIX = "\n";
    private static final String HEAD_WORD_SUFFIX = "\t";
    private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

    private static final IndexParser INDEX_PARSER = new IndexTabDilimeterParser();

    private final char[] CHAR_BUFFER = new char[CHAR_BUFFER_SIZE];
    private final StringBuilder builder = new StringBuilder(CHAR_BUFFER_SIZE);
    private BufferedReader reader;
    private final File indexFile;
}
