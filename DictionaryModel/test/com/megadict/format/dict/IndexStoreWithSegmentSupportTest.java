package com.megadict.format.dict;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.megadict.format.dict.index.Index;
import com.megadict.format.dict.index.IndexFile;

public class IndexStoreWithSegmentSupportTest {

    @Test
    public void testGetIndexOf() {
        IndexFile indexFile = IndexFile.makeFile("C:/test/av.index");

        IndexStore store = new IndexStoreWithSegmentSupport(indexFile);

        String[] testWords = {
                "zymurgy", "abort", "zoom", "test", "person", "00-database-info",
        };

        for (String testWord : testWords) {
            if (store.containsWord(testWord)) {
                Index foundIndex = store.getIndexOf(testWord);
                assertNotNull(foundIndex);
            }
        }
    }

    @Test
    public void testGetSimilarWord() {
        IndexFile indexFile = IndexFile.makeFile("C:/test/av.index");

        IndexStore store = new IndexStoreWithSegmentSupport(indexFile);

        String testWord = "hell";

        List<String> words = store.getSimilarWords(testWord, 20);

        assertNotNull(words);
        assertFalse(words.isEmpty());
    }

}
