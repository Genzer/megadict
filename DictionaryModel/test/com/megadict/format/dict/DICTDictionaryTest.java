package com.megadict.format.dict;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.megadict.model.Definition;
import com.megadict.model.Dictionary;

public class DICTDictionaryTest {
    
    private Dictionary testDict;
    
    @Before
    public void initialize() {
        String indexFilePath = "C:\\test\\av.index";
        String dictionaryFile = "C:\\test\\av-d.dict.dz";
        testDict = new DICTDictionary(indexFilePath, dictionaryFile);
    }

    @Ignore
    public void testDICTDictionary() {
        fail("Not yet implemented");
    }

    @Ignore
    public void testRecommendWord() {
        fail("Not yet implemented");
    }

    @Test
    public void testLookUp() {
        String testWord = "xxx";
        
        Definition def = testDict.lookUp(testWord);
        
        assertNotNull(def);
        assertNotSame(Definition.NOT_FOUND, def);
    }
    
    @Ignore
    public void testGetName() {
        fail("Not yet implemented");
    }

}
