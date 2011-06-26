package format.dict.parser;

import static org.junit.Assert.*;


import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import format.dict.index.Index;
import format.dict.parser.IndexParser;
import format.dict.parser.IndexTabDilimeterParser;

public class IndexTabDilimeterParserTest {
    
    private IndexParser testParser;
    
    private static final String SAMPLE_STRING = "00-database-info\tA\tNl";
    private static final Index SAMPLE_INDEX = new Index("00-database-info", 0, 869);
    
    @Before
    public void initialize() {
        testParser = new IndexTabDilimeterParser();
    }

    @Test
    public void testParseValidString() {
        Index parsedIndex = testParser.parse(SAMPLE_STRING);        
        assertEquals(SAMPLE_INDEX, parsedIndex);
    }
    
    @Ignore
    public void testParseInvalidString() {
        //TODO: The method to check whether the string is right format
        // is not yet implemented.
    }
    
    
}
