package com.megadict.format.dict.index;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.megadict.format.dict.parser.IndexParser;
import com.megadict.format.dict.parser.IndexTabDilimeterParser;
import com.megadict.format.dict.sample.index.IndexFileSample;
import com.megadict.format.dict.sample.index.Thesaurus;

public class IndexStoreTest {
    
    private IndexStore testee;
    private IndexParser parser;
    private IndexFileSample testSample;
    
    @Before
    public void initialize() {
        testSample = new Thesaurus();        
        testee = new IndexStore(testSample.getFilePath(), testSample.getTotalIndexes());
        
        parser = new IndexTabDilimeterParser(); 
    }

    @Test
    public void testGetIndexOf() {        
        Index expected = parser.parse(testSample.getSampleIndexString());        
        Index actual = testee.getIndexOf(testSample.getSampleHeadWord());        
        assertEquals(expected, actual);        
    }

}
