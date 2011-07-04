package com.megadict.format.dict;

import java.util.Collections;
import java.util.List;

import com.megadict.format.dict.index.Index;
import com.megadict.format.dict.index.IndexStore;
import com.megadict.format.dict.reader.DictionaryReader;
import com.megadict.format.dict.reader.RandomDictionaryReader;
import com.megadict.model.Definition;
import com.megadict.model.Dictionary;


public class DICTDictionary implements Dictionary {
    
    public DICTDictionary(String indexFile, String dictFile) {
        buildIndex(indexFile);
        definitionReader = new RandomDictionaryReader(dictFile);
        loadDictionaryMetadata();
    }
    
    private void buildIndex(String indexFile) {
        supportedWords = new IndexStore(indexFile);
    }
    
    
    private void loadDictionaryMetadata() {
        loadDictionaryName();
    }
    
    private void loadDictionaryName() {
        Index nameEntry = supportedWords.getIndexOf(MetaDataEntry.SHORT_NAME.tagName());
        String name = definitionReader.getDefinitionByIndex(nameEntry);
        this.name = name;
    }
    
    @Override
    public List<String> recommendWord(String word) {
        //TODO: Implement to return the adjacency words. Maybe
        // interpolation search algorithm.
        return Collections.emptyList();
    }
    
    @Override
    public Definition lookUp(String word) {
        if (supportedWords.containsWord(word)) {
            return loadDefinition(supportedWords.getIndexOf(word));
        } else {
            return Definition.NOT_FOUND;
        }
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
    private IndexStore supportedWords;
}
