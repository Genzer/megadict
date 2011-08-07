package com.megadict.test.toolbox.samples;

import java.io.File;
import java.util.Set;

public interface IndexTestSample {
    File getIndexFile();
    Set<String> getHeadwords();
    String getFullIndexStringOf(String headword);
}
