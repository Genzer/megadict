package format.dict.index.readStrategy;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadLineStrategy implements IndexReadingStrategy {

    public ReadLineStrategy(BufferedReader reader) {
        this.reader = reader;
    }
    
    @Override
    public List<String> readFullContent() throws IOException {
        String line = null;
        ArrayList<String> allLines = new ArrayList<String>();
        
        while( (line = reader.readLine()) != null) {
            allLines.add(line);
        }
        
        return allLines;
    }

    private final BufferedReader reader;
}
