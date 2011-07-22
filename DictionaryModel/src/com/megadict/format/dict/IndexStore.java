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

    private void buildCache() {
        cache = new TreeMap<String, Index>();
    }

    private void initializeReader(IndexFile indexFile) {
        reader = indexFile.getReader();
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

    public List<String> getSimiliarWord(String headword, int preferredNumber) {
        findInFileAndCacheEverythingFound(headword);
        Set<Map.Entry<String, Index>> filtered = filterSimilarWord(headword);
        return extractByPreferredNumber(filtered, preferredNumber);
    }

    private Set<Map.Entry<String, Index>> filterSimilarWord(String headword) {
        SortedMap<String, Index> tailMap = cache.tailMap(headword);
        return tailMap.entrySet();
    }

    private List<String> extractByPreferredNumber(Set<Map.Entry<String, Index>> filtered, int preferredNumber) {
        List<String> result = new ArrayList<String>(preferredNumber);
        
        int count = 0;
        for (Map.Entry<String, Index> entry : filtered) {
            result.add(entry.getKey());
            count++;
            if (count == preferredNumber) {
                break;
            }
        }
        
        return result;
    }

    private TreeMap<String, Index> cache;
    private IndexFileReader reader;
}
