package com.megadict.format.dict.index;

import java.util.*;

class BaseIndexStore implements IndexStore {

    public BaseIndexStore(IndexFile indexFile) {
        this.indexFile = indexFile;
        buildCache();
    }

    private void buildCache() {
        cache = new TreeMap<String, Index>();
    }

    @Override
    public Index getIndexOf(String headword) {
        if (containsWord(headword)) {
            return getFromCache(headword);
        } else {
            return null;
        }
    }

    @Override
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

    protected Set<Index> findInFile(String headword) {
        IndexFileReader reader = new IndexFileReader(indexFile.asRawFile());
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

    @Override
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

        for (int numOfItem = 0; numOfItem < preferredNumber && iterator.hasNext(); numOfItem++) {
            result.add(iterator.next().getKey());
        }

        return result;
    }

    protected final IndexFile indexFile;
    private TreeMap<String, Index> cache;
}
