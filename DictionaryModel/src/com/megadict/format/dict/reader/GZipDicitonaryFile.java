package com.megadict.format.dict.reader;

import java.io.File;

public class GZipDicitonaryFile extends DictionaryFile {

    public GZipDicitonaryFile(String dictFilePath) {
        super(dictFilePath);
    }
    
    public GZipDicitonaryFile(File dictFile) {
        super(dictFile);
    }

    @Override
    public DictFileReader getReader() {
        return new GZipDictFileReader(dictFile);
    }

}
