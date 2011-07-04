package com.megadict.format.dict;

import static org.junit.Assert.*;

import org.junit.*;

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

    @Test
    public void testRecommendWord() {
        fail("Not yet implemented");
    }
    
    @Test
    public void testGetName() {
        fail("Not yet implemented");
    }
    
    
    @Test
    public void testLookUpWithExistingWord() {
        String testWord = "alert";
        
        Definition def = testDict.lookUp(testWord);
        
        assertNotNull(def);
        assertNotSame(Definition.NOT_FOUND, def);
    }
    
    @Test
    public void testLookUpWithNonExistingWord() {
        String testWord = "xyas324";
        
        Definition def = testDict.lookUp(testWord);
        
        assertNotNull(def);
        assertSame(Definition.NOT_FOUND, def);
    }
    
    @Test
    public void testLookUpWithNullString() {
        String nullString = null;
        
        Definition def = testDict.lookUp(nullString);
        
        assertNotNull(def);
        assertSame(Definition.NOT_FOUND, def);
    }
    
    @Test
    public void testLookUpWithBlankString() {
        String blankString = "";
        
        Definition def = testDict.lookUp(blankString);
        
        assertNotNull(def);
        assertSame(Definition.NOT_FOUND, def);
    }
    
    @Test
    public void testLookUpWithStringContainsAllSpaces() {
        String allSpacesString = "   ";
        
        Definition def = testDict.lookUp(allSpacesString);
        
        assertNotNull(def);
        assertSame(Definition.NOT_FOUND, def);
    }

}
