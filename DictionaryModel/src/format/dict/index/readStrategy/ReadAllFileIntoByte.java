package format.dict.index.readStrategy;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class ReadAllFileIntoByte implements IndexReadingStrategy {
    
    public ReadAllFileIntoByte(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public List<String> readFullContent() throws IOException {
        return null;
    }
    
    
    @SuppressWarnings("unused")
    private byte[] readFileContent() {
        int bufferSize = 307200;
        
        byte[] readBytes = new byte[bufferSize];
        reader.hashCode();
        
        return null;       
    }
    
    private final BufferedReader reader;
}
