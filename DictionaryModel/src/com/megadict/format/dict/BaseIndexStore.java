package com.megadict.format.dict;

import java.util.*;

import com.megadict.format.dict.index.Index;
import com.megadict.format.dict.index.IndexFile;
import com.megadict.format.dict.index.IndexFileReader;
import com.megadict.format.dict.index.RegularIndexFileReader;

class BaseIndexStore implements IndexStore {

    protected final IndexFile indexFile;
    private final IndexCache cache = new IndexCache();

    public BaseIndexStore(IndexFile indexFile) {
        this.indexFile = indexFile;
        cacheFirstEntry();
    }

    private void cacheFirstEntry() {
        Index firstIndex = new FirstEntryReader(indexFile).read();
        cache(firstIndex);
    }

    @Override
    public Index getIndexOf(String headword) {
        return cache.get(headword);
    }

    @Override
    public boolean containsWord(String word) {
        if (findInCache(word) == false) {
            attempToFindInFileAndCache(word);
        }
        return findInCache(word);
    }

    private boolean findInCache(String word) {
        return cache.contains(word);
    }

    private void attempToFindInFileAndCache(String word) {
        Set<Index> foundInFile = findInFile(word);
        cacheAll(foundInFile);
    }

    protected Set<Index> findInFile(String headword) {
        IndexFileReader reader = new RegularIndexFileReader(indexFile.asRawFile());
        return reader.getIndexesSurrounding(headword);
    }

    private void cacheAll(Iterable<Index> indexes) {
        for (Index index : indexes) {
            cache(index);
        }
    }

    private void cache(Index index) {
        synchronized (cache) {
            cache.cache(index);
        }
    }

    @Override
    public List<String> getSimilarWord(String headword, int preferredNumber) {

        if (notEnoughSimilarWords(preferredNumber, headword)) {
            attempToFindInFileAndCache(headword);
        }
        
        return cache.getSimilarWord(headword, preferredNumber);
    }

    private boolean notEnoughSimilarWords(int preferredNumber, String headword) {
        int currentSimilarWords = cache.getSimilarWord(headword, preferredNumber).size();
        return currentSimilarWords < preferredNumber;
    }
}
