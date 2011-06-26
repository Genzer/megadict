package format.dict;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.junit.Ignore;
import org.junit.Test;

import format.dict.index.Index;
import format.dict.parser.IndexParser;
import format.dict.parser.IndexTabDilimeterParser;


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
