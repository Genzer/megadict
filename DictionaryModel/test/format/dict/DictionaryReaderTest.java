package format.dict;

import static org.junit.Assert.*;
import org.junit.*;

import format.dict.index.*;
import format.dict.parser.*;


public class DictionaryReaderTest {

    @Test
    public void testGetDefinitionByIndex() {
        final String filePath = "C:\\test\\av-d.dict.dz";
        
        final String indexString = "abbr\tsoEr\tBO";
        
        IndexParser parser = new IndexTabDilimeterParser();
        
        Index perIndex = parser.parse(indexString);
        assertNotNull(perIndex);
        
        System.out.println(perIndex);
        
        DictionaryReader reader = new DictionaryReader(filePath);
        
        String result = reader.getDefinitionByIndex(perIndex);
        
        //assertEquals(text, result);
        
        System.out.println(result);
        
    }
    
    @Test
    public void testCountString() {
        String test = "abbr\n" +
        		"- (vt của abbreviated, abbreviation) viết tắt, chữ viết tắt\n";
        System.out.println(test.length());
    }

}
