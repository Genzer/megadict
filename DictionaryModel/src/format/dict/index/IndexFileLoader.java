package format.dict.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import exception.ResourceMissingException;
import format.dict.index.readStrategy.CharReadingStrategy;
import format.dict.index.readStrategy.IndexReadingStrategy;

public class IndexFileLoader {

    public IndexFileLoader(String indexFilePath) {
        File inputFile = new File(indexFilePath);
        
        if (inputFile.exists()) {
            this.indexFile = inputFile;
        } else {
             abortConstructing();
        }
    }
    
    private void abortConstructing() {
        FileNotFoundException fileNotFound = new FileNotFoundException(indexFile + " is not found.");
        throw new ResourceMissingException("Index file", fileNotFound);
    }
    
    public List<String> load() {
        buildIndexes();       
        return indexes;
    }
    
    private void buildIndexes() {
        try {            
            readLinesAndPutIntoIndexes();
            closeReader();
        } catch (IOException rootCause) {
            throw new RuntimeException("Cannot read index file.", rootCause);
        }
    }
    
    private void readLinesAndPutIntoIndexes() throws IOException {        
        createReader();       
        performReading();
    }
    
    private void performReading() throws IOException {
        IndexReadingStrategy readStrategy = new CharReadingStrategy(textReader);
        indexes = readStrategy.readFullContent();
    }
    
    private void createReader() throws IOException {                
        this.textReader = new BufferedReader(new FileReader(indexFile));
    }
    
    private void closeReader() throws IOException {
        textReader.close();
    }
    
    private List<String> indexes;
    private File indexFile;
    private BufferedReader textReader;
    
}
