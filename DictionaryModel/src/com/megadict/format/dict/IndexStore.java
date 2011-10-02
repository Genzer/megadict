package com.megadict.format.dict;

import java.util.List;

import com.megadict.format.dict.index.Index;

interface IndexStore {
    
    public Index getIndexOf(String headword);

    public boolean containsWord(String word);

    public List<String> getSimilarWords(String headword, int preferredNumber);

}