package format.dict;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Definition;
import model.Dictionary;


public class DICTDictionary implements Dictionary {
    
    public static class Builder {
        private String indexFilePath;
        private String dictionaryFilePath;
        
        public Builder indexFile(String indexFilePath) {
            this.indexFilePath = indexFilePath;
            return this;
        }
        
        public Builder dictFile(String dictionaryFilePath) {
            this.dictionaryFilePath = dictionaryFilePath;
            return this;
        }
        
        public DICTDictionary build() {
            return new DICTDictionary(this);
        }
    }
    
    private DICTDictionary(Builder builder) {
        buildIndex(builder.indexFilePath);
        definitionReader = new DictionaryReader(builder.dictionaryFilePath);
        loadDictionaryMetadata();
    }
    
    private void buildIndex(String indexFile) {
        IndexBuilder builder = new IndexBuilder(indexFile);
        loadIndexes(builder.build());
    }
    
    private void loadIndexes(Set<Index> indexes) {
        supportedWords = new HashMap<String, Index>(indexes.size());
        
        for (Index index : indexes) {
            supportedWords.put(index.getWord(), index);
        }
    }
    
    private void loadDictionaryMetadata() {
        loadDictionaryName();
    }
    
    private void loadDictionaryName() {
        Index nameEntry = supportedWords.get(MetaDataEntry.SHORT_NAME.tagName());
        assert nameEntry != null;
        String name = definitionReader.getDefinitionByIndex(nameEntry);
        this.name = name;
    }

    @Override
    public Set<String> getAllWords() {
        return supportedWords.keySet();
    }
    
    @Override
    public List<String> recommendWord(String word) {
        //TODO: Implement to return the adjacency words. Maybe
        // interpolation search algorithm.
        return Collections.emptyList();
    }
    
    @Override
    public Definition lookUp(String word) {
        if (supportedWords.containsKey(word)) {
            return loadDefinition(supportedWords.get(word));
        }
        return null;
    }
    
    private Definition loadDefinition(Index index) {       
        String definitionContent = definitionReader.getDefinitionByIndex(index);
        return new Definition(index.getWord(), definitionContent, this.name);
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    private String name;
    private final DictionaryReader definitionReader;
    private Map<String, Index> supportedWords;
}
