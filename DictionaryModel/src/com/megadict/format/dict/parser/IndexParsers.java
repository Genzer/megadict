package com.megadict.format.dict.parser;

public class IndexParsers {

    private IndexParsers() {
    }
    
    public static IndexParser newInstance() {
        return new BackedByStringParser();
    }
}
