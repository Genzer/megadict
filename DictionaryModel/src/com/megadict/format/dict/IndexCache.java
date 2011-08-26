package com.megadict.format.dict;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.megadict.format.dict.index.Index;

class IndexCache {

    TreeMap<String, Index> innerCache = new TreeMap<String, Index>(String.CASE_INSENSITIVE_ORDER);

    public void cache(Index index) {
        innerCache.put(index.getWord(), index);
    }

    public boolean contains(String word) {
        return innerCache.containsKey(word);
    }

    public Index get(String word) {
        return innerCache.get(word);
    }

    public int countSimilarWords(String word) {
        return filterSimilarWord(word).size();
    }

    public List<String> getSimilarWord(String word, int preferredNumber) {
        return extractByPreferredNumber(filterSimilarWord(word), preferredNumber);
    }

    private Set<Map.Entry<String, Index>> filterSimilarWord(String headword) {
        return innerCache.tailMap(headword).entrySet();
    }

    private List<String> extractByPreferredNumber(Set<Map.Entry<String, Index>> filtered, int preferredNumber) {

        int size = Math.min(filtered.size(), preferredNumber);
        
        List<String> result = new ArrayList<String>(size);

        Iterator<Map.Entry<String, Index>> iterator = filtered.iterator();

        for (int numOfItem = 0; numOfItem < size; numOfItem++) {
            result.add(iterator.next().getKey());
        }

        return result;
    }
}
