package com.megadict.format.dict;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import com.megadict.format.dict.index.Index;
import com.megadict.format.dict.parser.IndexParser;
import com.megadict.format.dict.parser.IndexTabDilimeterParser;
import com.megadict.format.dict.reader.DictionaryFile;
import com.megadict.model.Definition;

public class DefinitionFinderTest {

    @Test
    public void testGetDefinitionAt() {
        
        File dictRawFile = new File("C:/test/av.dict");
        
        DictionaryFile dictFile = DictionaryFile.makeBufferedFile(dictRawFile);
        
        DefinitionFinder finder = new DefinitionFinder(dictFile.getReader());
        
        Index[] testIndexes = loadIndexes();
        
        for (Index index : testIndexes) {
            String content = finder.getDefinitionAt(index);
            assertNotNull(content);
            System.out.println(content);
        }
        
    }
    
    private Index[] loadIndexes() {
        
        String[] indexStrings = {
                "1-byte character\toN7/\tq",
                "reclassify\ty3Me\t4",
                "watch-dog\t14nk\tBx",
                "zymotic\toN2l\tBJ",
                "decorously\tuSf3\t4",
                "Forecast error\tqnR8\tDC",
                "procrastinating\tahbX\tBN",
                "two-master\tk+2K\tBU",
                "voidable\tm0Yz\tBa",
                "Z-score\tsmpF\tm",
        };
        
        IndexParser parser = new IndexTabDilimeterParser();
        
        Index[] result = new Index[indexStrings.length];
        
        for (int item = 0; item < indexStrings.length; item++) {
            result[item] = parser.parse(indexStrings[item]);
        }
        
        return result;
        
    }
    
    

}
