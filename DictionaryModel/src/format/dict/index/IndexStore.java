package format.dict.index;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.parser.Base64TextParser;

public class IndexStore {
    
    private static class Entry {
        
        public Entry(byte[] offset, byte[] length) {
           this.offset = offset;
           this.length = length;
        }
        
        public int getOffset() {
            return toInt(offset);
        }
        
        public int getLenght() {
            return toInt(length);
        }
        
        private int toInt(byte[] octets) {
            return Base64TextParser.toInt(octets);
        }
        
        private byte[] offset;
        private byte[] length;
    }
    
    public IndexStore(String indexFile) {
        buildIndexWithInitialCapacity(indexFile, 0);
    }
    
    public IndexStore(String indexFile, int totalIndexes) {
        buildIndexWithInitialCapacity(indexFile, totalIndexes);
    }   
    
    private void buildIndexWithInitialCapacity(String indexFile, int initialTotalIndexes) {
        IndexFileLoader builder = new IndexFileLoader(indexFile);
        List<String> rawIndexes = builder.load();
        entries = new HashMap<String, IndexStore.Entry>(initialTotalIndexes);
        putAllRawIndexesIntoMap(rawIndexes);
    }
    
    private void putAllRawIndexesIntoMap(List<String> rawIndexes) {
        for (String rawIndex : rawIndexes) {
            putRawIndexToMap(rawIndex);
        }
    }
    
   private void putRawIndexToMap(String rawIndex) {
       String[] splitted = rawIndex.split("\\t");
       Entry entry = new Entry(splitted[1].getBytes(), splitted[2].getBytes());
       entries.put(splitted[0], entry);
   }
    
    public Index getIndexOf(String headWord) {
        if (entries.containsKey(headWord)) {
            Entry entry = entries.get(headWord);
            return new Index(headWord, entry.getOffset(), entry.getLenght());
        }
        return null;
    }
    
    public int size() {
        return entries.size();
    }
    
    
    
    private Map<String, Entry> entries = Collections.emptyMap();
    
}
