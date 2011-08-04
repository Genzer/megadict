package com.megadict.format.dict.index;

import java.io.File;

public class IndexFile {
    
    protected IndexFile(String indexFilePath) {
        this(new File(indexFilePath));
    }
    
    protected IndexFile(File indexFile) {
        this.indexFile = indexFile;
    }
    
    public static IndexFile makeFile(String indexFilePath) {
        return new IndexFile(indexFilePath);
    }
    
    public static IndexFile makeFile(File indexFile) {
        return new IndexFile(indexFile);
    }

    public boolean exists() {
        return indexFile.exists();
    }
    
    public String getAbsolutePath() {
        return indexFile.getAbsolutePath();
    }
    
    public File asRawFile() {
        return indexFile;
    }
    
    @Override
    public String toString() {
        return indexFile.toString();
    }
        
    private final File indexFile;   
}
