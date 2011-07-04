package com.megadict.format.dict;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.megadict.format.dict.index.Index;
import com.megadict.format.dict.parser.IndexParser;
import com.megadict.format.dict.parser.IndexTabDilimeterParser;
import com.megadict.format.dict.reader.DictionaryReader;
import com.megadict.format.dict.reader.RandomDictionaryReader;


public class DictionaryReaderTest {

    @Test
    public void testGetDefinitionByIndex() {
        final String filePath = "C:\\test\\av-d.dict.dz";
        
        final String indexString = "abbr\tsoEr\tBO";
        
        IndexParser parser = new IndexTabDilimeterParser();
        
        Index perIndex = parser.parse(indexString);
        assertNotNull(perIndex);
        
        DictionaryReader reader = new RandomDictionaryReader(filePath);
        
        String result = reader.getDefinitionByIndex(perIndex);
        
        assertNotNull(result);
        
        System.out.println(result);
        
    }

}
