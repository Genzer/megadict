package com.megadict.format.dict;

import static org.junit.Assert.*;

import org.junit.*;

import com.megadict.model.Definition;
import com.megadict.model.Dictionary;

public class DICTDictionaryTest {
    
    private Dictionary testDict;
    
    final String hndIndexFile = "C:\\test\\av.index";
    final String hndDictFile = "C:\\test\\av-d.dict.dz";
    
    final String foraIndexFile = "C:\\test\\fora\\fora_ve.index";
    final String foraDictFile = "C:\\test\\fora\\fora_ve.dict.dz";
    
    @Before
    public void initialize() {
        testDict = new DICTDictionary(hndIndexFile, hndDictFile);
    }

    @Ignore @Test
    public void testRecommendWord() {
        fail("Not yet implemented");
    }
    
    @Ignore @Test
    public void testGetName() {
        fail("Not yet implemented");
    }
    
    
    @Ignore @Test
    public void testLookUpWithExistingWord() {
        String testWord = "con";
        
        Definition def = testDict.lookUp(testWord);
        
        assertNotNull(def);
        assertNotSame(Definition.NOT_FOUND, def);
        
        def = testDict.lookUp("co");
        assertNotNull(def);
        assertNotSame(Definition.NOT_FOUND, def);
    }
    
    @Ignore @Test
    public void testLookUpWithExistingWordContainsWhitespaces() {
        String wordContainsWhitespaces = "\"buddhist\" economy";
        
        Definition def = testDict.lookUp(wordContainsWhitespaces);
        
        assertNotNull(def);
        assertNotSame(Definition.NOT_FOUND, def);
    }
    
    @Ignore @Test
    public void testLookUpWithNonExistingWord() {
        String testWord = "xyas324";
        
        Definition def = testDict.lookUp(testWord);
        
        assertNotNull(def);
        assertSame(Definition.NOT_FOUND, def);
    }
    
    @Ignore @Test
    public void testLookUpWithNullString() {
        String nullString = null;
        
        Definition def = testDict.lookUp(nullString);
        
        assertNotNull(def);
        assertSame(Definition.NOT_FOUND, def);
    }
    
    @Ignore @Test
    public void testLookUpWithBlankString() {
        String blankString = "";
        
        Definition def = testDict.lookUp(blankString);
        
        assertNotNull(def);
        assertSame(Definition.NOT_FOUND, def);
    }
    
    @Ignore @Test
    public void testLookUpWithStringContainsAllSpaces() {
        String allSpacesString = "   ";
        
        Definition def = testDict.lookUp(allSpacesString);
        
        assertNotNull(def);
        assertSame(Definition.NOT_FOUND, def);
    }
    
    @Test
    public void testTakeTurnLookUpTwoDictWithSameWord() {
        String testWord = "con";
        
        Definition def = testDict.lookUp(testWord);
        
        assertNotNull(def);
        assertNotSame(Definition.NOT_FOUND, def);
        
        System.out.println(def.getContent());
        
        Dictionary foraDict;
        foraDict = new DICTDictionary(foraIndexFile, foraDictFile);
        Definition foraDef = foraDict.lookUp(testWord);
        assertNotNull(foraDef);
        assertNotSame(Definition.NOT_FOUND, foraDef);
        
        System.out.println(foraDef.getContent());
    }

}
