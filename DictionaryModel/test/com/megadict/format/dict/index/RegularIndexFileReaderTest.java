package com.megadict.format.dict.index;

import static org.junit.Assert.*;
import org.junit.*;

import java.util.*;

import com.megadict.format.dict.parser.*;
import com.megadict.test.toolbox.samples.IndexTestSample;
import com.megadict.test.toolbox.samples.TestSamples;

public class RegularIndexFileReaderTest {

    private IndexTestSample sampleSet;
    private IndexParser parser;
    private IndexFileReader testee;

    @Before
    public void prepareTests() {
        parser = IndexParsers.newParser();
        sampleSet = TestSamples.getCurrentIndexSample();
        testee = new RegularIndexFileReader(sampleSet.getIndexFile());
    }

    @Test
    public void testGettingIndexes() {
        Set<String> wordsToTest = sampleSet.getHeadwords();

        Set<Index> expectedParsedIndexes = parseToIndex(wordsToTest);

        Set<Index> actualParsedIndexes = processActualData(wordsToTest);

        assertActualValuesEqualExpected(actualParsedIndexes, expectedParsedIndexes);
    }

    private Set<Index> parseToIndex(Set<String> words) {

        Set<Index> expectedValues = new LinkedHashSet<Index>(words.size());

        for (String word : words) {
            String indexString = sampleSet.getFullIndexStringOf(word);
            expectedValues.add(makeIndex(indexString));
        }

        return expectedValues;
    }

    private Set<Index> processActualData(Set<String> wordsToTest) {

        Set<Index> actualValues = new LinkedHashSet<Index>(wordsToTest.size());

        for (String word : wordsToTest) {
            Index actualIndex = testee.getIndexOf(word);
            actualValues.add(actualIndex);
        }

        return actualValues;
    }

    private void assertActualValuesEqualExpected(Set<Index> actualValues,
            Set<Index> expectedValues) {
        Index[] actuals = new Index[actualValues.size()];
        actualValues.toArray(actuals);
        
        Index[] expecteds = new Index[expectedValues.size()];
        expectedValues.toArray(expecteds);
        
        for (int i = 0; i < actuals.length; i++) {
            assertEquals(expecteds[i], actuals[i]);
        }
    }

    private Index makeIndex(String index) {
        return parser.parse(index);
    }

    @Test
    public void testGetIndexOfWordThatNotExists() {
        String nonExistWord = "blablabla";

        Index found = testee.getIndexOf(nonExistWord);

        assertNull(found);
    }
    
    @Test
    public void testGetIndexesFromHeadword() {
        String word = "00-database-short";
        
        Set<Index> found = testee.getAdjacentIndexes(word);
        
        boolean empty = found.isEmpty();
        assertFalse(empty);
    }
    
}
