package com.megadict.format.dict.reader;

import java.io.File;


public abstract class DictionaryFile {
    
    protected DictionaryFile(String dictFilePath) {
        this.dictFile = new File(dictFilePath);
    }
    
    protected DictionaryFile(File dictFile) {
        this.dictFile = dictFile;
    }
    
    public static DictionaryFile makeRandomAccessFile(File dictFile) {
        return new RandomAccessDictionaryFile(dictFile);
    }
    
    public static DictionaryFile makeRandomAccessFile(String dictFilePath) {
        return new RandomAccessDictionaryFile(new File(dictFilePath));
    }
    
    public static DictionaryFile makeBufferedFile(File dictFile) {
        return new BufferedDictionaryFile(dictFile);
    }
    
    public static DictionaryFile makeBufferedFile(String dictFilePath) {
        return new BufferedDictionaryFile(new File(dictFilePath));
    }
    
    public static DictionaryFile makeGZipFile(File gzipFile) {
        return new GZipDicitonaryFile(gzipFile);
    }
    
    public static DictionaryFile makeGZipFile(String dictFilePath) {
        return new GZipDicitonaryFile(new File(dictFilePath));
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
