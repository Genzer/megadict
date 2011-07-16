package com.megadict.format.dict.index;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class IndexStore {
    
    public IndexStore(File indexFile) {
        this(indexFile, DEFAULT_CACHE_CAPACITY);       
    }
    
    public IndexStore(File indexFile, int totalIndexes) {
        buildCacheOfIndexesWithInitialCapacity(indexFile, totalIndexes);
        initializeReader(indexFile);
    }   
    
    private void buildCacheOfIndexesWithInitialCapacity(File indexFile, int initialTotalIndexes) {
        cache = new HashMap<String, Index>(initialTotalIndexes);
    }
    
    private void initializeReader(File indexFile) {
        reader = new IndexFileReader(indexFile);
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
