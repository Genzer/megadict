package com.megadict.format.dict.reader;

import java.io.File;


public abstract class DictionaryFile {
    
    public DictionaryFile(String dictFilePath) {
        this.dictFile = new File(dictFilePath);
    }
    
    public DictionaryFile(File dictFile) {
        this.dictFile = dictFile;
    }
    
    public boolean exists() {
        return dictFile.exists();
    }
    
    public String getAbsolutePath() {
        return dictFile.getAbsolutePath();
    }
    
    public String getFileName() {
        return dictFile.getName();
    }
    
    public abstract DictFileReader getReader();
    
    @Override
    public String toString() {
        return dictFile.toString();
    }
    
    protected final File dictFile;
}
