package format.dict.index;

import static org.junit.Assert.*;
import org.junit.*;
import format.dict.parser.*;
import format.dict.sample.index.*;

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
