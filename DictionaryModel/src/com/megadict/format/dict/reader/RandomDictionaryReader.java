package com.megadict.format.dict.reader;

import java.io.*;

public class RandomDictionaryReader extends BaseDictionaryReader 
        implements DictionaryReader {
    
    public RandomDictionaryReader(String dictionaryFile) {
        super(dictionaryFile);
    }
    
    @Override
    DictFileReader constructFileReader() throws FileNotFoundException {
        return new BufferedDictFileReader(dictionaryFile);
    }

}
