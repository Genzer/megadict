package com.megadict.test.toolbox.samples;

import java.io.File;
import java.util.Set;

public interface DictionaryTestSample extends IndexTestSample {
    
    String getDictionaryName();
    File getDictionaryFile();
    File getZippedDictionaryFile();
    
    Set<String> getSampleWords();
    Set<String> getSamplePhrases();
    Set<String> getNotExistingWords();
    
    String getActualContentOfTestWord(String word);
}
