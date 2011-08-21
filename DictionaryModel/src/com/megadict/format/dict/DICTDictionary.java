package com.megadict.format.dict;

import java.util.*;

import com.megadict.exception.*;
import com.megadict.format.dict.index.*;
import com.megadict.format.dict.reader.*;
import com.megadict.format.dict.util.StringChecker;
import com.megadict.model.*;
import com.megadict.model.Dictionary;

public class DICTDictionary implements Dictionary {
    
    public static class Builder {
        IndexFile indexFile;
        DictionaryFile dictFile;
        boolean segmentEnabled;
        
        public Builder(IndexFile indexFile, DictionaryFile dictionaryFile) {
            this.indexFile = indexFile;
            this.dictFile = dictionaryFile;
        }
        
        public Builder enableSplittingIndexFile() {
            segmentEnabled = true;
            return this;
        }
        
        public DICTDictionary build() {
            return new DICTDictionary(this);
        }
    }
    
    private DICTDictionary(Builder builder) {
        this.indexFile = builder.indexFile;
        this.dictFile  = builder.dictFile;
        if (builder.segmentEnabled) {
            initializeWithSegmentMode();
        } else {
            initialize();
        }
    }

    public DICTDictionary(IndexFile indexFile, DictionaryFile dictFile) {
        this.indexFile = indexFile;
        this.dictFile = dictFile;
        initialize();
    }
    
    private void initialize() {
        checkFileExistence();
        prepareIndexStore();
        prepareDefinitions();
        loadDictionaryMetadata();
    }
    
    private void initializeWithSegmentMode() {
        checkFileExistence();
        prepareIndexStoreWithSegment();
        prepareDefinitions();
        loadDictionaryMetadata();
    }

    private void checkFileExistence() {
        if (!(indexFile.exists())) {
            throw new ResourceMissingException(indexFile.toString());
        }

        if (!(dictFile.exists())) {
            throw new ResourceMissingException(dictFile.toString());
        }
    }

    private void prepareIndexStore() {
        supportedWords = IndexStores.newDefaultIndexStore(indexFile);
    }
    
    private void prepareIndexStoreWithSegment() {
        supportedWords = IndexStores.newIndexStoreSupportSegment(indexFile);
    }

    private void prepareDefinitions() {
        definitionFinder = new DefinitionFinder(dictFile.getReader());
    }

    private void loadDictionaryMetadata() {
        loadDictionaryName();
    }

    private void loadDictionaryName() {
        Index nameEntry = supportedWords.getIndexOf(MetaDataEntry.SHORT_NAME.tagName());
        String name = definitionFinder.getContentAt(nameEntry);
        this.name = cleanedUpName(name);
    }

    private static String cleanedUpName(String rawName) {
        String noNewLineCharactersName = rawName.replace("\n", "");
        StringBuilder builder = new StringBuilder(noNewLineCharactersName);
        builder.delete(0, NAME_REDUNDANT_STRING.length());
        return builder.toString();
    }

    @Override
    public List<String> recommendWord(String word) {
        return supportedWords.getSimilarWord(word, 20);
    }
    
    @Override
    public List<String> recommendWord(String word, int preferredNumOfWord) {
        return supportedWords.getSimilarWord(word, preferredNumOfWord);
    }

    @Override
    public Definition lookUp(String word) {
        boolean validated = validateWord(word);

        if (validated == false) {
            return Definition.NOT_FOUND;
        }

        Definition foundInDefinitionCache = findInDefinitionCache(word);
        if (foundInDefinitionCache != Definition.NOT_FOUND) {
            return foundInDefinitionCache;
        }

        if (supportedWords.containsWord(word)) {
            return loadAndCacheDefinition(supportedWords.getIndexOf(word));
        } else {
            return Definition.NOT_FOUND;
        }
    }

    private static boolean validateWord(String word) {
        return StringChecker.check(word);
    }

    private Definition findInDefinitionCache(String word) {
        if (definitionCache.containsKey(word)) {
            return definitionCache.get(word);
        } else {
            return Definition.NOT_FOUND;
        }
    }

    private Definition loadAndCacheDefinition(Index index) {
        String definitionContent = definitionFinder.getContentAt(index);
        Definition def = new Definition(index.getWord(), definitionContent, this.name);
        cacheDefinition(def);
        return def;
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
        return String.format(TO_STRING_PATTERN, name, indexFile, dictFile);
    }

    private final IndexFile indexFile;
    private final DictionaryFile dictFile;
    
    private String name;
    
    private IndexStore supportedWords;
    private DefinitionFinder definitionFinder;    
    
    private Map<String, Definition> definitionCache = new HashMap<String, Definition>();

    private static final String NAME_REDUNDANT_STRING = "@00-database-short- FVDP ";
    private static final String TO_STRING_PATTERN = "DICTDictionary[name: %s; indexFile: %s; dictFile: %s]";
}
