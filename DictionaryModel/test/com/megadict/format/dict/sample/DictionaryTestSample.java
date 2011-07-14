package com.megadict.format.dict.sample;

import java.io.File;
import java.util.Set;

public interface DictionaryTestSample {
    
    String getDictionaryName();
    File getDictionaryFile();
    File getZippedDictionaryFile();
    
    Set<String> getSampleWords();
    Set<String> getSamplePhrases();
    Set<String> getNotExistingWords();
    
    String getActualContentOfTestWord(String word);
}
