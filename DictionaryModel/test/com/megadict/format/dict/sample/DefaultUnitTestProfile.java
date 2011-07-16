package com.megadict.format.dict.sample;

import java.io.*;
import java.util.*;

class DefaultUnitTestProfile implements DictionaryTestSample {

    public DefaultUnitTestProfile(File profile) {
        this.profile = profile;
        loadContent();
    }
    
    private void loadContent() throws RuntimeException {
        XmlDocumentReader reader = new XmlDocumentReader(profile);
        
        dictionaryName = reader.getValueOf("profile/dictionaryName");        
        dictFile = makeFile(reader.getValueOf("profile/files/dict"));        
        zippedFile = makeFile(reader.getValueOf("profile/files/zipped"));
        indexFile = makeFile(reader.getValueOf("profile/files/index"));
        
        sampleWords = loadSampleContent(reader.getValueOf("profile/sampleSet/word"));
        samplePhrases = loadSampleContent(reader.getValueOf("profile/sampleSet/phrase"));
        notExistingWords = loadSampleContent(reader.getValueOf("profile/sampleSet/notExisting"));        
    }
    
    private File makeFile(String dictFilePath) {
        return new File(makeFullPath(dictFilePath));
    }    
    
    private String makeFullPath(String sampleFile) {
        return profile.getParent() + "\\" + sampleFile;
    }
    
    private Map<String, Sample> loadSampleContent(String sampleFile) {
        String fullSampleFilePath = makeFullPath(sampleFile);
        return loadSampleFileContent(fullSampleFilePath);
    }
    
    private Map<String, Sample> loadSampleFileContent(String fullFilePath) {
        return new SampleWordLoader(new File(fullFilePath)).loadContent();
    }
    
    public String getDictionaryName() {
        return dictionaryName;
    }

    public File getDictionaryFile() {
        return dictFile;
    }

    public File getZippedDictionaryFile() {
        return zippedFile;
    }

    public Set<String> getSampleWords() {
        return sampleWords.keySet();
    }

    public Set<String> getSamplePhrases() {
        return samplePhrases.keySet();
    }

    public Set<String> getNotExistingWords() {
        return notExistingWords.keySet();
    }

    public String getActualContentOfTestWord(String word) {
        return findSample(word).getExcpectedContent();
    }
    
    private Sample findSample(String key) {
        Sample found = null;
        if (sampleWords.containsKey(key)) {
            found = sampleWords.get(key);
        } else if (samplePhrases.containsKey(key)) {
            found = samplePhrases.get(key);
        } else if (notExistingWords.containsKey(key)) {
            found = notExistingWords.get(key);
        }
        return found;
    }
    
    @Override
    public File getIndexFile() {
        return indexFile; 
    }

    @Override
    public Set<String> getHeadwords() {
        int size = sampleWords.size() + samplePhrases.size();
        Set<String> result = new HashSet<String>(size);
        result.addAll(sampleWords.keySet());
        result.addAll(samplePhrases.keySet());
        return result;
    }

    @Override
    public String getFullIndexStringOf(String headword) {
        return findSample(headword).getIndexString();
    }
    
    private File profile;
    
    private String dictionaryName = "";
    private File dictFile;
    private File zippedFile;
    private File indexFile;
    
    private Map<String, Sample> sampleWords = Collections.emptyMap();
    private Map<String, Sample> samplePhrases = Collections.emptyMap();
    private Map<String, Sample> notExistingWords = Collections.emptyMap();
    
}
