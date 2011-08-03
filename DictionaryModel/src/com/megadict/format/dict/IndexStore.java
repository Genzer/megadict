package com.megadict.format.dict;

import java.util.*;

import com.megadict.format.dict.index.Index;
import com.megadict.format.dict.index.IndexFile;
import com.megadict.format.dict.index.IndexFileReader;

class IndexStore {

    public IndexStore(IndexFile indexFile) {
        initializeReader(indexFile);
        buildCache();
    }

    private void initializeReader(IndexFile indexFile) {
        reader = indexFile.getReader();
    }
    
    private void buildCache() {
        cache = new TreeMap<String, Index>();
    }
    
    
    

    public Index getIndexOf(String headword) {
        if (containsWord(headword)) {
            return getFromCache(headword);
        } else {
            return null;
        }
    }

    public boolean containsWord(String word) {
        if (findInCache(word) == false) {
            findInFileAndCacheEverythingFound(word);
        }
        return findInCache(word);
    }

    private boolean findInCache(String word) {
        return cache.containsKey(word);
    }

    private void findInFileAndCacheEverythingFound(String word) {
        Set<Index> foundIndexes = findInFile(word);
        cacheAll(foundIndexes);
    }

    private Set<Index> findInFile(String headword) {
        return reader.getIndexesStartFrom(headword);
    }

    private void cacheAll(Iterable<Index> indexes) {
        for (Index index : indexes) {
            cache.put(index.getWord(), index);
        }
    }

    private Index getFromCache(String headword) {
        return cache.get(headword);
    }

    
    
    
    public List<String> getSimilarWord(String headword, int preferredNumber) {
        findInFileAndCacheEverythingFound(headword);
        Set<Map.Entry<String, Index>> filtered = filterSimilarWord(headword);
        return extractByPreferredNumber(filtered, preferredNumber);
    }

    private Set<Map.Entry<String, Index>> filterSimilarWord(String headword) {
        return cache.tailMap(headword).entrySet();
    }

    private List<String> extractByPreferredNumber(Set<Map.Entry<String, Index>> filtered, int preferredNumber) {
        List<String> result = new ArrayList<String>(preferredNumber);
        
        Iterator<Map.Entry<String, Index>> iterator = filtered.iterator();
        
        for(int numOfItem = 0;
                numOfItem < preferredNumber && iterator.hasNext(); numOfItem++) {
            result.add(iterator.next().getKey());
        }
        
        return result;
    }

    private TreeMap<String, Index> cache;
    private IndexFileReader reader;
}
