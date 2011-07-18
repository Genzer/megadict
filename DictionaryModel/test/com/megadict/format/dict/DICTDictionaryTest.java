package com.megadict.format.dict;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.*;

import com.megadict.format.dict.index.IndexFile;
import com.megadict.format.dict.reader.DictionaryFile;
import com.megadict.format.dict.sample.*;
import com.megadict.model.*;

public class DICTDictionaryTest {
    
    private Dictionary testee;
    private DictionaryTestSample sampleTest;
    
    @Before
    public void setUp() {
        sampleTest = TestSamples.getCurrentDictionarySample();
        
        IndexFile indexFile = IndexFile.makeFile(sampleTest.getIndexFile());
        DictionaryFile dictFile = DictionaryFile.makeBufferedFile(sampleTest.getDictionaryFile());
        
        testee = new DICTDictionary(indexFile, dictFile);
    }

    @Ignore ("not yet implemented") @Test
    public void testRecommendWord() {
        fail("Not yet implemented");
    }
    
    @Test
    public void testGetName() {
        String actualName = testee.getName();      
        assertEquals(sampleTest.getDictionaryName(), actualName);
    }
    
    
    @Test
    public void testLookUpWithExistingWord() {
        Set<String> words = sampleTest.getSampleWords();
        
        for (String word : words) {
            Definition actualDef = testee.lookUp(word);
            String expectedContent = sampleTest.getActualContentOfTestWord(word);
            assertDefActualContentEqualsExpected(actualDef, expectedContent);
        }
    }
    
    private static void assertDefActualContentEqualsExpected(Definition actualDef, String expected) {
        assertNotNull(actualDef);
        assertEquals(expected, actualDef.getContent());
    }
    
    @Test
    public void testLookUpWithExistingPhrases() {
        Set<String> phrases = sampleTest.getSamplePhrases();
        
        for (String phrase : phrases) {
            Definition actualDef = testee.lookUp(phrase);
            String expectedContent = sampleTest.getActualContentOfTestWord(phrase);
            assertDefActualContentEqualsExpected(actualDef, expectedContent);
        }
    }
    
    @Test
    public void testLookUpNotExistingWords() {
        Set<String> notExistingWords = sampleTest.getNotExistingWords();
        
        for (String word : notExistingWords) {
            Definition shouldBeNotFound = testee.lookUp(word);
            assertActualDefinitionIsNotFound(shouldBeNotFound);
        }
    }
    
    private static void assertActualDefinitionIsNotFound(Definition actual) {
        assertNotNull(actual);
        assertSame(Definition.NOT_FOUND, actual);
    }
    
    @Test
    public void testLookUpWithNullString() {
        String nullString = null;
        
        Definition shouldBeNotFound = testee.lookUp(nullString);
        
        assertActualDefinitionIsNotFound(shouldBeNotFound);
    }
    
    @Test
    public void testLookUpWithBlankString() {
        String blankString = "";
        
        Definition shouldBeNotFound = testee.lookUp(blankString);
        
        assertActualDefinitionIsNotFound(shouldBeNotFound);
    }
    
    @Test
    public void testLookUpWithStringContainsAllSpaces() {
        String onlySpacesText = "   ";
        
        Definition shouldBeNotFound = testee.lookUp(onlySpacesText);
        
        assertActualDefinitionIsNotFound(shouldBeNotFound);
    }

}
