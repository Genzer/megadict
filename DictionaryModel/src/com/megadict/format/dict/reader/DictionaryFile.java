package com.megadict.format.dict.reader;

import java.io.File;

public class DictionaryFile {

    private final File dictFile;
    private final DictFileReader reader;

    private DictionaryFile(File dictFile, DictFileReader reader) {
        this.dictFile = dictFile;
        this.reader = reader;
    }

    public static DictionaryFile makeRandomAccessFile(File dictFile) {
        DictFileReader randomReader = new MappedDictFileReader(dictFile);
        return new DictionaryFile(dictFile, randomReader);
    }

    public static DictionaryFile makeRandomAccessFile(String dictFilePath) {
        return makeRandomAccessFile(new File(dictFilePath));
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

    public DictFileReader getReader() {
        return this.reader;
    }

    @Override
    public String toString() {
        return dictFile.toString();
    }
}
