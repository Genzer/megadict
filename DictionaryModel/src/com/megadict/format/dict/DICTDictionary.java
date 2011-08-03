package com.megadict.format.dict;

import java.util.*;

import com.megadict.exception.*;
import com.megadict.format.dict.index.*;
import com.megadict.format.dict.reader.*;
import com.megadict.model.*;
import com.megadict.model.Dictionary;

public class DICTDictionary implements Dictionary {

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

    private void checkFileExistence() {
        if (!(indexFile.exists())) {
            throw new ResourceMissingException("Index file " + indexFile);
        }

        if (!(dictFile.exists())) {
            throw new ResourceMissingException("Dict file " + dictFile);
        }
    }

    private void prepareIndexStore() {
        supportedWords = new IndexStore(indexFile);
    }

    private void prepareDefinitions() {
        definitionFinder = new DefinitionFinder(dictFile.getReader());
    }

    private void loadDictionaryMetadata() {
        loadDictionaryName();
    }

    private void loadDictionaryName() {
        Index nameEntry = supportedWords.getIndexOf(MetaDataEntry.SHORT_NAME.tagName());
        String name = definitionFinder.getDefinitionAt(nameEntry);
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
        String definitionContent = definitionFinder.getDefinitionAt(index);
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
        if (toStringCache == null) {
            toStringCache = String.format(TO_STRING_PATTERN, name, indexFile, dictFile);
        }
        return toStringCache;
    }

    private final IndexFile indexFile;
    private final DictionaryFile dictFile;
    private String name;
    private DefinitionFinder definitionFinder;
    private Map<String, Definition> definitionCache = new HashMap<String, Definition>();
    private IndexStore supportedWords;

    private static final String NAME_REDUNDANT_STRING = "@00-database-short- FVDP ";
    private static final String TO_STRING_PATTERN = "DICTDictionary[name: %s; indexFile: %s; dictFile: %s]";
    private String toStringCache;
}
