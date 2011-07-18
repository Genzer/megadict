package com.megadict.format.dict.reader;

import java.io.File;


public class BufferedDictionaryFile extends DictionaryFile {

    public BufferedDictionaryFile(String dictFilePath) {
        super(dictFilePath);
    }
    
    public BufferedDictionaryFile(File dictFile) {
        super(dictFile);
    }

    @Override
    public DictFileReader getReader() {
        return new BufferedDictFileReader(dictFile);
    }

}
