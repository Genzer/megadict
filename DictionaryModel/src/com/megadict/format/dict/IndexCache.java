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

    public List<String> getSimilarWord(String word, int preferredNumber) {
        
        Set<Map.Entry<String, Index>> adjacents = adjacentWords(word);

        int size = Math.min(adjacents.size(), preferredNumber);
        
        List<String> similarWords = new ArrayList<String>(size);

        Iterator<Map.Entry<String, Index>> iterator = adjacents.iterator();        
        
        for (int numOfItem = 0; (numOfItem < size) && iterator.hasNext(); numOfItem++) {
            
            String adjacent = iterator.next().getKey();   
            
            if (adjacent.startsWith(word)) {
                similarWords.add(adjacent);
            }
            
        }
        
        return similarWords;
    }

    private Set<Map.Entry<String, Index>> adjacentWords(String headword) {
        return innerCache.tailMap(headword).entrySet();
    }
}
