package com.megadict.format.dict.reader;

import java.io.FileNotFoundException;

public class ZippedDictionaryReader extends BaseDictionaryReader 
        implements DictionaryReader {
    
    public ZippedDictionaryReader(String dictionaryFile) {
        super(dictionaryFile);
    }

    @Override
    DictFileReader constructFileReader() throws FileNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }
 
}
