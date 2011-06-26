package format.dict.index;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import format.dict.index.Index;
import format.dict.index.IndexStore;
import format.dict.parser.IndexParser;
import format.dict.parser.IndexTabDilimeterParser;
import format.dict.sample.index.IndexFileSample;
import format.dict.sample.index.PackageIncluded;
import format.dict.sample.index.Thesaurus;

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

    @Ignore
    public void testGetIndexOf() {        
        Index expected = parser.parse(testSample.getSampleIndexString());        
        Index actual = testee.getIndexOf(testSample.getSampleHeadWord());        
        assertEquals(expected, actual);        
    }

    @Test
    public void testSize() {
        assertEquals(testSample.getTotalIndexes(), testee.size());
    }

}
