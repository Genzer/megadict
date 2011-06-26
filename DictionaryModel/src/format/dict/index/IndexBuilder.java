package format.dict.index;

import java.io.*;
import java.util.*;

import exception.ResourceMissingException;
import format.dict.parser.IndexParser;
import format.dict.parser.IndexTabDilimeterParser;

public class IndexBuilder {

    public IndexBuilder(String indexFilePath) {
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
    
    public Set<Index> build() {    
        buildIndexes();       
        return indexes;
    }
    
    private void buildIndexes() {
        try {            
            readLinesAndParseToIndexes();
            closeReader();
        } catch (IOException rootCause) {
            throw new RuntimeException("Cannot read index file.", rootCause);
        }
    }
    
    private void readLinesAndParseToIndexes() throws IOException {
        IndexParser parser = new IndexTabDilimeterParser();
        String readLine;
        
        createReader();      
        
        while( (readLine = textReader.readLine()) != null) {
            Index newIndex = parser.parse(readLine);
            indexes.add(newIndex);
        }
    }
    
    private void createReader() throws IOException {                
        this.textReader = new BufferedReader(new FileReader(indexFile));
    }
    
    private void closeReader() throws IOException {
        textReader.close();
    }
    
    private Set<Index> indexes = new HashSet<Index>();;
    private File indexFile;
    private BufferedReader textReader;
    
}
