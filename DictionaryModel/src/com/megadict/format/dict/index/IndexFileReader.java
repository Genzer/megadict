package com.megadict.format.dict.index;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;

import com.megadict.exception.ClosingFileException;
import com.megadict.exception.ReadingIndexFileException;
import com.megadict.exception.ResourceMissingException;
import com.megadict.format.dict.parser.IndexParser;
import com.megadict.format.dict.parser.IndexTabDilimeterParser;

class IndexFileReader {

    public IndexFileReader(String indexFilePath) {
        this.indexFilePath = indexFilePath;
    }

    public Index getIndexOf(String headWord) {
        String indexString = find(headWord);
        if (indexString != null) {
            return INDEX_PARSER.parse(indexString);
        } else {
            return null;
        }

    }

    public String getIndexStringOf(String word) {
        return find(word);
    }

    private String find(String headWord) throws ReadingIndexFileException,
            ResourceMissingException {
        try {
            makeReader();
            String found = locateIndexStringOf(headWord);
            return found;
        } catch (FileNotFoundException fnf) {
            throw new ResourceMissingException(indexFilePath, fnf);
        } catch (IOException ioException) {
            throw new ReadingIndexFileException(indexFilePath, ioException);
        } finally {
            //closeReader();
        }
    }

    private void makeReader() throws FileNotFoundException {
        if (reader == null) {
            FileInputStream rawStream = new FileInputStream(indexFilePath);
            reader = new BufferedReader(newUnicodeStream(rawStream),
                    READER_INTERNAL_BUFFER_SIZE);
        }
    }

    private InputStreamReader newUnicodeStream(FileInputStream rawStream) {
        return new InputStreamReader(rawStream, UTF8_CHARSET);
    }

    private String locateIndexStringOf(String headWord) throws IOException {

        headWord = matchingWholeWord(headWord);
        resetBuffer(CHAR_BUFFER);
        resetBuilder();

        while (stillReading()) {
            builder.append(CHAR_BUFFER);

            int foundPosition = builder.indexOf(headWord);

            if (foundPosition != -1) {
                return readFullLineAt(foundPosition);
            } else {
                resetBuilder();
            }
        }

        return null;
    }

    private static String matchingWholeWord(String headWord) {
        return HEAD_WORD_PREFIX + headWord + HEAD_WORD_SUFFIX;
    }

    private static void resetBuffer(char[] buffer) {
        Arrays.fill(buffer, ' ');
    }

    private boolean stillReading() throws IOException {
        return reader.read(CHAR_BUFFER) != -1;
    }

    private String readFullLineAt(int startPosition) {
        // Avoid heading new line character "\n".
        startPosition++;

        int endPosition = startPosition + 100;

        int currentBufferLength = builder.length();
        // TODO: It's the case when the finding record is half-read,
        // and we have to continue read. Should use a marker.
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
            throw new ClosingFileException(indexFilePath, ioe);
        }
    }
    
    void close() {
        closeReader();
    }

    private static final int READER_INTERNAL_BUFFER_SIZE = 8 * 1024;
    private static final int BUFFER_SIZE = 5000;
    
    private static final String HEAD_WORD_PREFIX = "\n";
    private static final String HEAD_WORD_SUFFIX = "\t";    
    private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");
    
    private static final IndexParser INDEX_PARSER = new IndexTabDilimeterParser();
    
    private final char[] CHAR_BUFFER = new char[BUFFER_SIZE];
    private final StringBuilder builder = new StringBuilder(BUFFER_SIZE);
    private BufferedReader reader;
    private final String indexFilePath;
}
