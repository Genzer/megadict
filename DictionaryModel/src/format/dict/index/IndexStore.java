package format.dict.index;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class IndexStore {
    
    public IndexStore(String indexFile) {
        this(indexFile, DEFAUT_CAPACITY);       
    }
    
    public IndexStore(String indexFile, int totalIndexes) {
        buildIndexWithInitialCapacity(indexFile, totalIndexes);
        initialReader(indexFile);
    }   
    
    private void buildIndexWithInitialCapacity(String indexFile, int initialTotalIndexes) {
        cache = new HashMap<String, Index>(initialTotalIndexes);
    }
    
    private void initialReader(String indexFile) {
        reader = new IndexFileReader(indexFile);
    }
    
    public Index getIndexOf(String headWord) {
        if (containsWord(headWord)) {
            return cache.get(headWord);
        } else {
            return null;
        }
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
    
    public int size() {
        return cache.size();
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
    
    private static final int DEFAUT_CAPACITY = 1000;
    private Map<String, Index> cache = Collections.emptyMap();
    private IndexFileReader reader;
    
}
