package model.parser;

import static org.junit.Assert.*;

import org.junit.Test;

public class IndexTabDilimeterParserTest {

    @Test
    public void testParse() {
        final String testTwo = "pers\tx06V\tBT";
        IndexParser parser = new IndexTabDilimeterParser();
        
        Index parsedIndex = parser.parse(testTwo);
        
        assertNotNull(parsedIndex);
        
        System.out.println(parsedIndex);
    }

}
