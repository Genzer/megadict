package format.dict;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Definition;
import model.Dictionary;


public class DICTDictionary implements Dictionary {
    
    public DICTDictionary(String indexFile, String dictionaryFile) {
        initializeIndex(indexFile);
        definitionReader = new DictionaryReader(dictionaryFile);
    }
    
    private void initializeIndex(String indexFile) {
        IndexBuilder builder = new IndexBuilder(indexFile);
        loadIndexes(builder.build());
    }
    
    private void loadIndexes(Set<Index> indexes) {
        supportedWords = new HashMap<String, Index>(indexes.size());
        
        for (Index index : indexes) {
            supportedWords.put(index.getWord(), index);
        }
    }

    @Override
    public Set<String> getAllWords() {
        return supportedWords.keySet();
    }
    
    @Override
    public List<String> recommendWord(String word) {
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
