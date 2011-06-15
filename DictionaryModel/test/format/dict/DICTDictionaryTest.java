package format.dict;

import static org.junit.Assert.*;

import java.util.Set;

import model.Definition;
import model.Dictionary;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class DICTDictionaryTest {
    
    private Dictionary testDict;
    
    @Before
    public void initialize() {
        String indexFilePath = "C:\\test\\av.index";
        String dictionaryFile = "C:\\test\\av-d.dict.dz";
        testDict = new DICTDictionary(indexFilePath, dictionaryFile);
    }

    @Ignore
    public void testDICTDictionary() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetAllWords() {
        Set<String> allWords = testDict.getAllWords();
        assertNotNull(allWords);
        System.out.println(allWords.size());
    }

    @Ignore
    public void testRecommendWord() {
        fail("Not yet implemented");
    }

    @Test
    public void testLookUp() {
        String testWord = "pers";
        
        Definition def = testDict.lookUp(testWord);
        
        assertNotNull(def);
        System.out.println(def.getContent());
    }

}
