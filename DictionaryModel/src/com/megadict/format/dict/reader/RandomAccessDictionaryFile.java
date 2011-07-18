package com.megadict.format.dict.reader;

import java.io.File;


class RandomAccessDictionaryFile extends DictionaryFile {

    public RandomAccessDictionaryFile(String dictFilePath) {
        super(dictFilePath);
    }
    
    public RandomAccessDictionaryFile(File dictFile) {
        super(dictFile);
    }

    @Override
    public DictFileReader getReader() {
        return new RandomDictFileReader(dictFile);
    }

}
