package com.megadict.format.dict.index;

import java.io.File;


public class IndexFile {
    
    public IndexFile(String indexFilePath) {
        this.indexFile = new File(indexFilePath);
    }
    
    public IndexFile(File indexFile) {
        this.indexFile = indexFile;
    }
    
    public boolean exists() {
        return indexFile.exists();
    }
    
    public String getAbsolutePath() {
        return indexFile.getAbsolutePath();
    }
    
    public IndexFileReader getReader() {
        return new IndexFileReader(indexFile);
    }
    
    @Override
    public String toString() {
        return indexFile.toString();
    }
        
    private final File indexFile;
    
}
