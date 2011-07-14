package com.megadict.format.dict.sample;

import java.io.File;
import java.util.Set;

import com.megadict.format.dict.index.Index;

public interface IndexTestSample {
    File getIndexFile();
    Set<String> getHeadwords();
    String getFullIndexStringOf(String headword);
    Index getActualIndexOf(String headword);
}
