package com.megadict.format.dict;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.megadict.format.dict.index.Index;
import com.megadict.format.dict.index.IndexFile;
import com.megadict.format.dict.index.IndexFileReader;

class IndexStore {
    
    public IndexStore(IndexFile indexFile) {
        this(indexFile, DEFAULT_CACHE_CAPACITY);       
    }
    
    public IndexStore(IndexFile indexFile, int totalIndexes) {
        buildCacheOfIndexesWithInitialCapacity(indexFile, totalIndexes);
        initializeReader(indexFile);
    }   
    
    private void buildCacheOfIndexesWithInitialCapacity(IndexFile indexFile, int initialTotalIndexes) {
        cache = new HashMap<String, Index>(initialTotalIndexes);
    }
    
    private void initializeReader(IndexFile indexFile) {
        reader = indexFile.getReader();
    }
    
    public Index getIndexOf(String headWord) {
        if (containsWord(headWord)) {
            return cache.get(headWord);
        } else {
            return null;
        }
    }
    
    public boolean containsWord(String word) {
        boolean foundInCache = cache.containsKey(word);
        
        if (foundInCache == false) {
            Index findInFile = findInFile(word);
            if (findInFile != null) {
                cache(findInFile);
                return true;
            }
        }
        return foundInCache;
    }    
    
    private Index findInFile(String headWord) {
        Index foundIndex = reader.getIndexOf(headWord);
        if (foundIndex != null) {
            cache(foundIndex);
            return foundIndex;
        } else {
            return null;
        }
    }
    
    private void cache(Index index) {
        cache.put(index.getWord(), index);
    }
    
    private static final int DEFAULT_CACHE_CAPACITY = 1000;
    private Map<String, Index> cache = Collections.emptyMap();
    private IndexFileReader reader;    
}
