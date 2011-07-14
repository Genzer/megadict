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
        
        dictionaryName = reader.getElementAt("profile/dictionaryName").getText();        
        dictFile = makeDictFile(reader.getElementAt("profile/files/dict").getText());        
        zippedFile = makeDictFile(reader.getElementAt("profile/files/zipped").getText());
        
        sampleWords = loadContentOf(reader.getElementAt("profile/sampleSet/word").getText());
        samplePhrases = loadContentOf(reader.getElementAt("profile/sampleSet/phrase").getText());
        notExistingWords = loadContentOf(reader.getElementAt("profile/sampleSet/notExisting").getText());        
    }
    
    private File makeDictFile(String dictFilePath) {
        return new File(makeFullPath(dictFilePath));
    }    
    
    private String makeFullPath(String sampleFile) {
        return profile.getParent() + "\\" + sampleFile;
    }
    
    private Map<String, String> loadContentOf(String sampleFile) {
        String fullSampleFilePath = makeFullPath(sampleFile);
        return loadSampleFileContent(fullSampleFilePath);
    }
    
    private Map<String, String> loadSampleFileContent(String fullFilePath) {
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
        if (sampleWords.containsKey(word)) {
            return sampleWords.get(word);
        } else if (samplePhrases.containsKey(word)) {
            return samplePhrases.get(word);
        } else if (notExistingWords.containsKey(word)) {
            return notExistingWords.get(word);
        } else {
            return "";
        }
    }
    
    private File profile;
    
    private String dictionaryName = "";
    private File dictFile;
    private File zippedFile;
    
    private Map<String, String> sampleWords = Collections.emptyMap();
    private Map<String, String> samplePhrases = Collections.emptyMap();
    private Map<String, String> notExistingWords = Collections.emptyMap();
    
}
