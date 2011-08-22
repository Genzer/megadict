package com.megadict.format.dict;

import static com.megadict.model.Definition.NOT_FOUND;

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
        this.name =
                (nameEntry == NOT_FOUND) ? "Unknown Dictionary" : cleanedUpName(nameEntry.getContent());
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
        Definition result = validateWord(word) ? find(word) : NOT_FOUND;
        definitionCache.cache(word, result);
        return result;
    }

    private static boolean validateWord(String word) {
        return StringChecker.check(word);
    }

    private Definition find(String word) {

        Definition result = NOT_FOUND;

        if (definitionCache.contains(word)) {
            result = definitionCache.get(word);

        } else if (supportedWords.containsWord(word)) {
            Index index = supportedWords.getIndexOf(word);
            result = loadDefinitionAt(index);
        }

        return result;
    }

    private Definition loadDefinitionAt(Index index) {
        String definitionContent = definitionFinder.getContentAt(index);
        return new Definition(index.getWord(), definitionContent, this.name);
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
    private final IndexStore supportedWords;
    private final DefinitionFinder definitionFinder;
    private final DefinitionCache definitionCache = new DefinitionCache();

    private static final String NAME_REDUNDANT_STRING = "@00-database-short- FVDP ";
    private static final String TO_STRING_PATTERN = "DICTDictionary[name: %s; indexFile: %s; dictFile: %s]";
}
