package com.megadict.format.dict.reader;

import com.megadict.format.dict.index.Index;

public interface DictionaryReader {
    public String getDefinitionByIndex(Index index);
}