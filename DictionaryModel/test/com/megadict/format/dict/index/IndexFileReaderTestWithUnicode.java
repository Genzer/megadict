package com.megadict.format.dict.index;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import com.megadict.format.dict.reader.DictionaryReader;
import com.megadict.format.dict.reader.RandomDictionaryReader;
import com.megadict.model.Definition;

public class IndexFileReaderTestWithUnicode {

    @Test
    public void testGetIndexOf() {
        String indexFile = "C:\\test\\hnd\\ve\\ve.index";
        
        IndexFileReader fileReader = new IndexFileReader(indexFile);
        
        String result = fileReader.getIndexStringOf("con");
        
        assertNotNull(result);
        
        System.out.println(result);
    }

    @Ignore ("Not feel like testing it yet") 
    @Test
    public void testGetIndexStringOf() {
        fail("Not yet implemented");
    }
    
    @Test
    public void testRetrievingContentFromDictFile() {
        String indexFile = "C:\\test\\hnd\\ve\\ve.index";
        String dictFile = "C:\\test\\hnd\\ve\\ve-d.dict.dz";
        
        String findString = "con";
        
        IndexFileReader fileReader = new IndexFileReader(indexFile);        
        DictionaryReader dictReader = new RandomDictionaryReader(dictFile);
        
        Index result = fileReader.getIndexOf(findString);
        assertNotNull(result);
        
        String def = dictReader.getDefinitionByIndex(result);
        assertNotNull(def);
        System.out.println(def);        
    }

}
