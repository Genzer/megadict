package format.dict.index;

import java.io.*;
import java.util.*;

import exception.ResourceMissingException;
import format.dict.index.readStrategy.IndexReadingStrategy;
import format.dict.index.readStrategy.ReadLineStrategy;

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
        IndexReadingStrategy readStrategy = new ReadLineStrategy(textReader);
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
