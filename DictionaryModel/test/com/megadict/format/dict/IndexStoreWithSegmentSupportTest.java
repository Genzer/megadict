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
            Index foundIndex = store.getIndexOf(testWord);
            assertNotNull(foundIndex);
            System.out.println(foundIndex);
        }
    }

    @Test
    public void testGetSimilarWord() {
        IndexFile indexFile = IndexFile.makeFile("C:/test/av.index");

        IndexStore store = new IndexStoreWithSegmentSupport(indexFile);
        
        String testWord = "hell";
        
        List<String> words = store.getSimilarWord(testWord, 20);
        
        assertNotNull(words);
        assertFalse(words.isEmpty());
        
        for (String word : words) {
            System.out.println(word);
        }
    }

}
