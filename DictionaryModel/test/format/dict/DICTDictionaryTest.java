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
//        String indexFilePath = "C:\\test\\av.index";
//        String dictionaryFile = "C:\\test\\av-d.dict.dz";
//        testDict = new DICTDictionary(indexFilePath, dictionaryFile);
        
        String thesaurusDict = "C:\\test\\dictd\\moby-thesaurus.dict.dz";
        String thesaurusIndex = "C:\\test\\dictd\\moby-thesaurus.index";
        
        testDict = new DICTDictionary.Builder().indexFile(thesaurusIndex).dictFile(thesaurusDict).build();
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
        String testWord = "abet";
        
        Definition def = testDict.lookUp(testWord);
        
        assertNotNull(def);
        System.out.println(def.getContent());
    }
    
    @Test
    public void testGetName() {
        String expectedName = "Moby Thesaurus II by Grady Ward, 1.0";
        
        String dictionaryName = testDict.getName();
        
        assertEquals(expectedName, dictionaryName);
    }

}
