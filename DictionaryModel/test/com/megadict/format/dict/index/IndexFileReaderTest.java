package com.megadict.format.dict.index;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.megadict.format.dict.parser.IndexParser;
import com.megadict.format.dict.parser.IndexTabDilimeterParser;

public class IndexFileReaderTest {

    private IndexParser parser;
    private String indexTestFile;
    private IndexFileReader testee;

    @Before
    public void prepareTests() {
        parser = new IndexTabDilimeterParser();
        indexTestFile = "C:\\test\\av.index";
        testee = new IndexFileReader(indexTestFile);
    }

    @Test
    public void testFoundWordsInFile() {
        String[] wordsToFind = loadTestHeadWords();   
        
        Index[] expectedValues = loadExpectedIndexes();
        
        Index[] actualValues = processActualData(wordsToFind);
        
        assertActualValuesEqualExpected(actualValues, expectedValues);
    }

    private String[] loadTestHeadWords() {
        return new String[] { 
                "abbey", "perplexed", "zygospore", "zymology",
                "zymometer", "zoography", "yucky", "yid",
                "fundamental wavelength", "Function income distribution",
                "full-length", "bipart", "biometrically", "biometrician",
                "remonstrating" };
    }
    
    private Index[] loadExpectedIndexes() {
        String[] testIndexStrings = { "abbey\tCAY\tBo", "perplexed\tYzfM\tBr",
                "zygospore\toNvE\tBW", "zymology\toNya\tBC",
                "zymometer\toNzc\tBC", "zoography\toNBv\tBJ", "yucky\t2FGG\tv",
                "yid\t2E1a\tt", "fundamental wavelength\tpcAp\t8",
                "Function income distribution\tqqw5\tBS",
                "full-length\tNgJx\tDz", "bipart\t2QCw\tV",
                "biometrically\ttQsP\tg", "biometrician\tDJjl\tBH",
                "remonstrating\tcds3\tB4" };
        
        Index[] expectedValues = new Index[testIndexStrings.length];
        
        for (int row = 0; row < expectedValues.length; row++) {
            expectedValues[row] = makeIndex(testIndexStrings[row]);
        }
        
        return expectedValues;
     }
    
    private Index[] processActualData(String[] wordsToFind) {
        Index[] actualValues = new Index[wordsToFind.length];
        
        for (int row = 0; row < wordsToFind.length; row++) {
            actualValues[row] = testee.getIndexOf(wordsToFind[row]);
        }
        
        return actualValues;
    }
    
    private void assertActualValuesEqualExpected(Index[] actualValues, Index[] expectedValues) {
        for (int row = 0; row < actualValues.length; row++) {
            assertEquals(expectedValues[row], actualValues[row]);
        }
    }

    private Index makeIndex(String index) {
        return parser.parse(index);
    }
    
    @Test
    public void testNotFoundWordInFile() {
        String nonExistWord = "okaydokay";
        
        Index found = testee.getIndexOf(nonExistWord);
        
        assertNull(found);
    }

}
