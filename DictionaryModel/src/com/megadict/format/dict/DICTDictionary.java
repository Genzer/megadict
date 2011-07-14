package com.megadict.format.dict;

import java.util.*;

import com.megadict.format.dict.index.Index;
import com.megadict.format.dict.index.IndexStore;
import com.megadict.format.dict.reader.DictionaryReader;
import com.megadict.format.dict.reader.RandomDictionaryReader;
import com.megadict.model.Definition;
import com.megadict.model.Dictionary;

public class DICTDictionary implements Dictionary {

    public DICTDictionary(String indexFile, String dictFile) {
        this.indexFile = indexFile;
        this.dictFile = dictFile;
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
        Index nameEntry = supportedWords.getIndexOf(MetaDataEntry.SHORT_NAME
                .tagName());
        String name = definitionReader.getDefinitionByIndex(nameEntry);
        this.name = name;
    }

    @Override
    public List<String> recommendWord(String word) {
        // TODO: Implement to return the adjacency words. Maybe
        // interpolation search algorithm.
        return Collections.emptyList();
    }

    @Override
    public Definition lookUp(String word) {
        boolean validated = validateWord(word);

        if (validated && supportedWords.containsWord(word)) {
            return loadDefinition(supportedWords.getIndexOf(word));
        } else {
            return Definition.NOT_FOUND;
        }
    }

    private static boolean validateWord(String word) {
        return StringChecker.check(word);
    }

    private Definition loadDefinition(Index index) {
        if (definitionCache.containsKey(index)) {
            return definitionCache.get(index);
        } else {
            String definitionContent = definitionReader
                    .getDefinitionByIndex(index);
            Definition def = new Definition(index.getWord(), definitionContent,
                    this.name);
            cacheDefinition(def);
            return def;
        }
    }

    private void cacheDefinition(Definition def) {
        definitionCache.put(def.getWord(), def);
    }

    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public String toString() {
        if (toStringCache == null) {
            toStringCache = String.format(toStringPattern, name, indexFile, dictFile);
        }
        return toStringCache;
    }

    private final String indexFile;
    private final String dictFile;
    private String name;
    private final DictionaryReader definitionReader;
    private Map<String, Definition> definitionCache = new HashMap<String, Definition>();
    private IndexStore supportedWords;
    
    private final String toStringPattern = "DICTDictionary[name: %s; indexFile: %s; dictFile: %s]";
    private String toStringCache;
}
