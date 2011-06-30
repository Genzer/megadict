package format.dict.index.readStrategy;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CharReadingStrategy implements IndexReadingStrategy {

    public CharReadingStrategy(BufferedReader reader) {
        this.reader = reader;
    }
    
    @Override
    public List<String> readFullContent() throws IOException {
        return Arrays.asList(allLinesOfFile());
    }
    
    private String[] allLinesOfFile() throws IOException {
        String fullContent = performReading();
        return fullContent.split("\n");
    }
    
    private String performReading() throws IOException {
        char[] charBuffer = new char[CHAR_BUFFER_SIZE];
       
        StringBuilder fullContent = new StringBuilder();
        
        while (reader.read(charBuffer) != -1) {
            fullContent.append(charBuffer);
            Arrays.fill(charBuffer, '\n');
        }
        
        return fullContent.toString();
    }
    
    private static final int CHAR_BUFFER_SIZE = 30;
    private final BufferedReader reader;

}
