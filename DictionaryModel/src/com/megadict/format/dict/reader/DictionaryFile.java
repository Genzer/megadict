package com.megadict.format.dict.reader;

import java.io.File;

public class DictionaryFile {
    
    private DictionaryFile(File dictFile, DictFileReader reader) {
        this.dictFile = dictFile;
        this.reader = reader;
    }
    
    public static DictionaryFile makeRandomAccessFile(File dictFile) {
        RandomDictFileReader randomReader = new RandomDictFileReader(dictFile);
        return new DictionaryFile(dictFile, randomReader);
    }
    
    public static DictionaryFile makeRandomAccessFile(String dictFilePath) {
        return makeRandomAccessFile(new File(dictFilePath));
    }
    
    public static DictionaryFile makeBufferedFile(File dictFile) {
        BufferedDictFileReader bufferedReader = new BufferedDictFileReader(dictFile);
        return new DictionaryFile(dictFile, bufferedReader);
    }
    
    public static DictionaryFile makeBufferedFile(String dictFilePath) {
        return makeBufferedFile(new File(dictFilePath));
    }
    
    public static DictionaryFile makeGZipFile(File gzipFile) {
        GZipDictFileReader gzipReader = new GZipDictFileReader(gzipFile);
        return new DictionaryFile(gzipFile, gzipReader);
    }
    
    public static DictionaryFile makeGZipFile(String dictFilePath) {
        return makeGZipFile(new File(dictFilePath));
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
    
    public  DictFileReader getReader() {
        return this.reader;
    }
    
    @Override
    public String toString() {
        return dictFile.toString();
    }
    
    private final File dictFile;
    private final DictFileReader reader;
}
