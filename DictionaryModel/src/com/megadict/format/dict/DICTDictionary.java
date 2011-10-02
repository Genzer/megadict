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

        private final IndexFile indexFile;
        private final DictionaryFile dictFile;

        private DefinitionFinder defFinder;
        private IndexStore indexStore;

        private boolean segmentEnabled;

        public Builder(IndexFile indexFile, DictionaryFile dictionaryFile) {
            this.indexFile = indexFile;
            this.dictFile = dictionaryFile;
        }

        public Builder enableSplittingIndexFile() {
            segmentEnabled = true;
            return this;
        }

        public DICTDictionary build() {
            checkFileExistence();
            defFinder = new DefinitionFinder(dictFile.getReader());
            indexStore = newIndexStore();
            return new DICTDictionary(this);
        }

        private void checkFileExistence() {
            if (!(indexFile.exists())) {
                throw new ResourceMissingException(indexFile.toString());
            } else if (!(dictFile.exists())) {
                throw new ResourceMissingException(dictFile.toString());
            }
        }

        private IndexStore newIndexStore() {
            return segmentEnabled ? new IndexStoreWithSegmentSupport(indexFile) : new BaseIndexStore(indexFile);
        }

    }

    private static final String[] NAME_REDUNDANT_STRINGS = { "@00-database-short", "FVDP " };

    private static final String NOT_FOUND_CONTENT_PATTERN = "There is no definition of \"%s\"";
    private static final String TO_STRING_PATTERN = "DICTDictionary[name: %s; indexFile: %s; dictFile: %s]";

    private String name;
    private final IndexStore supportedWords;
    private final DefinitionFinder definitionFinder;
    private final DefinitionCache definitionCache = new DefinitionCache();
    private final IndexFile indexFile;
    private final DictionaryFile dictFile;

    private DICTDictionary(Builder builder) {
        this.indexFile = builder.indexFile;
        this.dictFile = builder.dictFile;
        this.definitionFinder = builder.defFinder;
        this.supportedWords = builder.indexStore;
        loadDictionaryName();
    }

    private void loadDictionaryName() {
        String nameKeyword = MetaDataEntry.SHORT_NAME.tagName();
        Definition nameEntry = lookUp(nameKeyword);

        boolean nameIsNotFound = nameEntry.getContent().contains("There is no definition");

        this.name =
                (nameIsNotFound) ? "Unknown Dictionary" : cleanedUpName(nameEntry.getContent());
    }

    private static String cleanedUpName(String rawName) {
        String[] NAME_REDUNDANT_STRINGS = { "00-database-short", "FVDP" };

        String noNewLineCharactersName = rawName.replaceAll("(\n|- |@)", "");

        StringBuilder builder = new StringBuilder(noNewLineCharactersName);

        for (String redundant : NAME_REDUNDANT_STRINGS) {
            int index = builder.indexOf(redundant);

            if (index != -1) {
                builder.delete(index, redundant.length());
            }
        }

        return builder.toString().trim();
    }

    @Override
    public List<String> recommendWord(String word) {
        return supportedWords.getSimilarWords(word, 20);
    }

    @Override
    public List<String> recommendWord(String word, int preferredNumOfWord) {
        synchronized (supportedWords) {
            return supportedWords.getSimilarWords(word, preferredNumOfWord);
        }
    }

    @Override
    public Definition lookUp(String word) {
        synchronized (supportedWords) {
            Definition result = validateWord(word) ? performLookup(word) : makeNotFound(word);
            definitionCache.cache(word, result);
            return result;
        }
    }

    private static boolean validateWord(String word) {
        return StringChecker.check(word);
    }

    private Definition performLookup(String word) {

        Definition result = null;

        if (definitionCache.contains(word)) {
            result = definitionCache.get(word);

        } else if (supportedWords.containsWord(word)) {
            Index index = supportedWords.getIndexOf(word);
            result = loadDefinitionAt(index);
        }

        return (result != null) ? result : makeNotFound(word);
    }

    private Definition makeNotFound(String word) {
        String content = String.format(NOT_FOUND_CONTENT_PATTERN, word);
        return Definition.makeNonExists(word, content, this.name);
    }

    private Definition makeDefinition(String word, String content) {
        return Definition.makeDefinition(word, content, this.name);
    }

    private Definition loadDefinitionAt(Index index) {
        String definitionContent = definitionFinder.getContentAt(index);
        return makeDefinition(index.getWord(), definitionContent);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return String.format(TO_STRING_PATTERN, name, indexFile, dictFile);
    }
}
