package com.megadict.format.dict;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.megadict.format.dict.index.IndexFile;
import com.megadict.format.dict.reader.DictionaryFile;
import com.megadict.model.Dictionary;

public class DICTDictionaryTestBugs {

    @Test
    public void testWrongResultRecommendingWithVEDict() {

        IndexFile indexFile = IndexFile.makeFile("C:/test/av.index");

        DictionaryFile dictFile = DictionaryFile.makeRandomAccessFile("C:/test/av.dict");

        Dictionary dict = new DICTDictionary.Builder(indexFile, dictFile).enableSplittingIndexFile().build();

        List<String> result = dict.recommendWord("zoom");

        System.out.println(result);
    }

    @Test
    public void testWrongResultRecommending() {
        
        IndexFile indexFile = IndexFile.makeFile("C:/test/av.index");

        DictionaryFile dictFile = DictionaryFile.makeRandomAccessFile("C:/test/av.dict");

        Dictionary dict = new DICTDictionary.Builder(indexFile, dictFile).enableSplittingIndexFile().build();

        List<String> result = dict.recommendWord("test");
        
        System.out.println(result);
        
        List<String> smallerScope = dict.recommendWord("testing");
        
        System.out.println(smallerScope);
    }
    
    

}
