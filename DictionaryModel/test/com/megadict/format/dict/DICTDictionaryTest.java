package com.megadict.format.dict;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.*;
import org.junit.rules.ExpectedException;

import com.megadict.exception.ResourceMissingException;
import com.megadict.format.dict.index.IndexFile;
import com.megadict.format.dict.reader.DictionaryFile;
import com.megadict.format.dict.sample.*;
import com.megadict.model.*;

public class DICTDictionaryTest {

    private Dictionary testee;
    private DictionaryTestSample sampleTest;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        sampleTest = TestSamples.getCurrentDictionarySample();

        IndexFile indexFile = IndexFile.makeFile(sampleTest.getIndexFile());
        DictionaryFile dictFile = DictionaryFile.makeRandomAccessFile(sampleTest.getDictionaryFile());

        testee = new DICTDictionary.Builder(indexFile, dictFile).enableSplittingIndexFile().build();
    }

    @Test
    public void testRecommendWord() {
        String[] expectedSimilarWords = {
                "a",
                "a 51.840 mbit/s sonet optical signal (oc-1)",
                "a 622.080mbit/s sonet optical signal (oc-12)",
                "a 933.120mbit/s sonet optical signal (oc-18)",
                "a amplifier",
                "a b c",
                "a b c - book",
                "a battery",
                "a block",
                "a body committed to accelerate the development of the qsig standard by providing coordinated input to ecma (ipns forum)" };
        
        String word = "a";
        List<String> actualSimilarWords = testee.recommendWord(word);
        
        assertEveryItemInActualListEqualsExpected(actualSimilarWords, Arrays.asList(expectedSimilarWords));
    }
    
    private void assertEveryItemInActualListEqualsExpected(List<String> actual, List<String> expected) {
        String[] actuals = new String[actual.size()];
        actual.toArray(actuals);
        
        String[] expecteds = new String[expected.size()];
        expected.toArray(expecteds);
        
        for (int i = 0; i < expecteds.length; i++) {
            assertEquals(actuals[i], expecteds[i]);
        }
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

    @Test
    public void testCreateWithNotExistingIndexFile() throws ResourceMissingException {
        expectedException.expect(ResourceMissingException.class);
        expectedException.expectMessage("Index file C:\\not\\exists\\file.index does not exist.");

        IndexFile notExist = IndexFile.makeFile("C:/not/exists/file.index");
        DictionaryFile dictFile = DictionaryFile.makeBufferedFile(sampleTest.getDictionaryFile());
        new DICTDictionary(notExist, dictFile);
    }

    @Test
    public void testCreateWithNotExistingDictFile() throws ResourceMissingException {
        expectedException.expect(ResourceMissingException.class);
        expectedException.expectMessage("Dict file C:\\not\\exists\\file.dict does not exist.");

        IndexFile indexFile = IndexFile.makeFile(sampleTest.getIndexFile());
        DictionaryFile notExist = DictionaryFile.makeBufferedFile("C:/not/exists/file.dict");
        new DICTDictionary(indexFile, notExist);
    }

}
