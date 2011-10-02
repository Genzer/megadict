package com.megadict.format.dict;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.*;

import com.megadict.format.dict.index.Index;
import com.megadict.format.dict.index.IndexFile;
import com.megadict.format.dict.index.IndexFileReader;
import com.megadict.format.dict.index.RandomIndexFileReader;
import com.megadict.format.dict.index.segment.Segment;
import com.megadict.format.dict.index.segment.SegmentStore;
import com.megadict.format.dict.reader.DictionaryFile;
import com.megadict.model.Definition;
import com.megadict.model.Dictionary;

public class DICTDictionaryTestBugs {

    @Test
    public void testWrongResultRecommendingWithVEDict() {

        IndexFile indexFile = IndexFile.makeFile("C:/test/foldoc.index");

        DictionaryFile dictFile = DictionaryFile.makeRandomAccessFile("C:/test/foldoc.dict");

        Dictionary dict = new DICTDictionary.Builder(indexFile, dictFile).enableSplittingIndexFile().build();

        System.out.println(dict.getName());
    }

    @Ignore @Test
    public void testWrongResultRecommending() {
        
        IndexFile indexFile = IndexFile.makeFile("C:/test/av.index");

        DictionaryFile dictFile = DictionaryFile.makeRandomAccessFile("C:/test/av.dict");

        Dictionary dict = new DICTDictionary.Builder(indexFile, dictFile).enableSplittingIndexFile().build();

        List<String> result = dict.recommendWord("test");
        
        System.out.println(result);
        
        List<String> smallerScope = dict.recommendWord("testing");
        
        System.out.println(smallerScope);
    }
    
    @Ignore @Test
    public void testFailToLookUpVietnameseDictionary() {
        
        final Segment givenSegment = new Segment("hiểu", "họng", 180224, 8192);
        final String givenWord = "học tập";
        
        SegmentStore mockedSegmentStore = new SegmentStore(Arrays.asList(givenSegment)) {
            @Override
            public Segment findSegmentPossiblyContains(String word) {
                return givenSegment;
            }
        };
        
        File indexFile = new File("C:/test/ve.index");
        
        IndexFileReader reader = new RandomIndexFileReader(indexFile, mockedSegmentStore);
        
        Index result = reader.getIndexOf(givenWord);
        
        System.out.println(result);
    }

}
