package com.megadict.format.dict.parser;

import static org.junit.Assert.*;
import org.junit.*;

import com.megadict.format.dict.index.Index;

public class IndexTabDilimeterParserTest {
    
    private static final String SAMPLE_STRING = "00-database-info\tA\tNl";
    private static final Index SAMPLE_INDEX = new Index("00-database-info", 0, 869);

    @Test
    public void testParseValidString() {
        IndexParser testParser = new IndexTabDilimeterParser();
        Index parsedIndex = testParser.parse(SAMPLE_STRING);        
        assertEquals(SAMPLE_INDEX, parsedIndex);
    }
    
    @Test
    public void testParseInvalidString() {        
        String invalidFormat = "234234 23423\t23dd\t24 \t";
        IndexParser parser = new IndexTabDilimeterParser();
        Index parsedIndex = parser.parse(invalidFormat);
        assertNull(parsedIndex);
    }
    
    @Test
    public void testParseStringWithInvalidBase64Encoding() {
        String invalidString = "invalid\t234?\t234";
        IndexParser parser = new IndexTabDilimeterParser();
        Index parsedIndex = parser.parse(invalidString);
        assertNull(parsedIndex);
    }
    
    
}
