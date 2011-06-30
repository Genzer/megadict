package format.dict.index;

import java.io.*;
import java.util.Arrays;

import format.dict.parser.IndexParser;
import format.dict.parser.IndexTabDilimeterParser;

public class IndexFileReader {

    public IndexFileReader(String indexFilePath) {
        this.indexFilePath = indexFilePath;
    }

    public Index getIndexOf(String headWord) {
        String indexString = find(headWord);
        if (indexString != null) {
            return parser.parse(indexString);
        } else {
            return null;
        }
        
    }

    public String getIndexStringOf(String word) {
        return find(word);
    }

    private String find(String headWord) {
        try {
            String found = locateIndexStringOf(headWord);
            closeReader();
            return found;
        } catch (FileNotFoundException fileMissing) {
            throw new RuntimeException("Cannot find the file " + indexFilePath,
                    fileMissing);
        } catch (IOException ioException) {
            throw new RuntimeException("There's a problem when finding word ["
                    + headWord + "]", ioException);
        }
    }

    private String locateIndexStringOf(String headWord) throws IOException {

        headWord = optimizeHeadWordForFinding(headWord);
        resetBuffer(CHAR_BUFFER);
        reader = makeReader();

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

    private static String optimizeHeadWordForFinding(String headWord) {
        return HEAD_WORD_PREFIX + headWord;
    }

    private static void resetBuffer(char[] buffer) {
        Arrays.fill(buffer, ' ');
    }

    private BufferedReader makeReader() throws FileNotFoundException {
        return new BufferedReader(new FileReader(indexFilePath));
    }

    private boolean stillReading() throws IOException {
        return reader.read(CHAR_BUFFER) != -1;
    }

    private String readFullLineAt(int startPosition) {
        // Avoid heading new line character "\n".
        startPosition++;

        int endPosition = startPosition + 100;
        
        int currentBufferLength = builder.length();
        //TODO: It's the case when the finding record is half-read,
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
    
    private void closeReader() throws IOException {
        reader.close();
    }

    private static final IndexParser parser = new IndexTabDilimeterParser();
    private static final String HEAD_WORD_PREFIX = "\n";
    private static final int BUFFER_SIZE = 5000;
    private static final char[] CHAR_BUFFER = new char[BUFFER_SIZE];
    private static final StringBuilder builder = new StringBuilder(BUFFER_SIZE);

    private BufferedReader reader;
    private final String indexFilePath;
}
