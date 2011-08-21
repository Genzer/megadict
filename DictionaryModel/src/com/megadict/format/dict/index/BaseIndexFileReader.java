package com.megadict.format.dict.index;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.megadict.exception.OperationFailedException;
import com.megadict.exception.ParseIndexException;
import com.megadict.exception.ResourceMissingException;
import com.megadict.format.dict.parser.IndexParser;
import com.megadict.format.dict.parser.IndexParsers;
import com.megadict.format.dict.util.FileUtil;

abstract class BaseIndexFileReader implements IndexFileReader {

    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private static final String HEAD_WORD_PREFIX = "\n";
    private static final String HEAD_WORD_SUFFIX = "\t";
    
    protected static final int CHAR_BUFFER_SIZE;
    private static final int NUM_OF_CHAR_TO_BE_READ_ON;
    
    protected final StringBuilder builder = new StringBuilder(CHAR_BUFFER_SIZE);
    private final IndexParser indexParser = IndexParsers.newParser();
    protected final File indexFile;
    
    static {
        CHAR_BUFFER_SIZE = determineCharBufferSize(FileUtil.DEFAULT_BUFFER_SIZE_IN_BYTES);
        NUM_OF_CHAR_TO_BE_READ_ON = determineNumOfCharShouldBeReadOn(100);
    }

    private static int determineCharBufferSize(int bufferSizeInBytes) {
        int sizeOfCharInBytes = 2;
        return bufferSizeInBytes / sizeOfCharInBytes;
    }

    private static int determineNumOfCharShouldBeReadOn(int numOfWordShouldReadOn) {
        int numOfCharPerWord = 10;
        return numOfWordShouldReadOn * numOfCharPerWord;
    }
    
    public BaseIndexFileReader(File indexFile) {
        this.indexFile = indexFile;
    }
    
    
    @Override
    public Index getIndexOf(String headword) {
        String indexString = readExactlyOne(headword);
        return (indexString != null) ? makeNewIndex(indexString) : null;
    }

    @Override
    public Set<Index> getIndexesSurrounding(String headwordInclusive) {

        Set<Index> indexes = new HashSet<Index>();
        String[] indexStrings = readSurroundingOf(headwordInclusive);

        for (String indexString : indexStrings) {
            parseToIndexIfPossibleAndAddToSet(indexString, indexes);
        }

        return indexes;
    }
    
    private Index makeNewIndex(String indexString) {
        try {
            return indexParser.parse(indexString);
        } catch (ParseIndexException pie) {
            return null;
        }
    }
    
    private String readExactlyOne(String headword) {        
        String instrumentedHeadword = createExactMatching(headword);
        int foundPosition = indexOf(instrumentedHeadword);
        return readExtractOneIndexStringAt(foundPosition);
    }
    
    protected String[] readSurroundingOf(String headword) {
        String customedheadword = createFuzzyMatching(headword);
        int foundPosition = indexOf(customedheadword);
        return readAsManyIndexStringAsPossible(foundPosition);
    }
    
    private static String createExactMatching(String headword) {
        return HEAD_WORD_PREFIX + headword + HEAD_WORD_SUFFIX;
    }
    
    private static String createFuzzyMatching(String headword) {
        return HEAD_WORD_PREFIX + headword;
    }
    
    private int indexOf(String headword) {
        try {
            makeReader();
            int foundPosition = locateIndexStringOf(headword);
            return foundPosition;
        } catch (FileNotFoundException fnf) {
            throw new ResourceMissingException(indexFile, fnf);
        } catch (IOException ioException) {
            throw new OperationFailedException("reading index file", ioException);
        } finally {
            closeReader();
        }
    }
    
    protected abstract void makeReader();
   
    protected abstract int locateIndexStringOf(String headword) throws IOException;
    
    protected abstract void closeReader();
    
    private String readExtractOneIndexStringAt(int startPosition) {
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
        
        // Ignore heading new line character ("\n").
        startPosition++;

        int candidateEndPosition = startPosition + NUM_OF_CHAR_TO_BE_READ_ON;

        int endPosition = Math.min(candidateEndPosition, builder.length());

        String rawString = builder.substring(startPosition, endPosition).trim();

        return rawString.split("\n");
    }
    
    private void parseToIndexIfPossibleAndAddToSet(String indexString, Set<Index> indexes) {
        Index newIndex = makeNewIndex(indexString);
        if (newIndex != null) {
            indexes.add(newIndex);
        }
    }
}
