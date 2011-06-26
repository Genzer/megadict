package format.dict.index;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import exception.ResourceMissingException;
import format.dict.index.IndexFileLoader;
import format.dict.sample.index.EngVi;
import format.dict.sample.index.ForaDictIndex;
import format.dict.sample.index.IndexFileSample;
import format.dict.sample.index.Thesaurus;

public class IndexFileLoaderTest {
    
    private IndexFileSample currentTestSample;
    private IndexFileLoader testBuilder;    
    private List<String> builtIndexes; 
    
    @Before
    public void initialize() {
        currentTestSample = new  ForaDictIndex();
    }
  
    @Test (expected = ResourceMissingException.class)
    public void testNotFoundIndexFile() throws ResourceMissingException {
        constructBuilderWithPath("this is a crap string");
    }
    
    private void constructBuilderWithPath(String file) {
        this.testBuilder = new IndexFileLoader(file);
    }
    
    @Test
    public void testBuildIndexFile() {
        constructBuilder();
        doBuildIndex();
        assertBuiltIndexIsNotNull();
    }
    
    private void constructBuilder() {
        constructBuilderWithPath(currentTestSample.getFilePath());
    }
    
    private void doBuildIndex() {
        builtIndexes = testBuilder.load();      
    }
    
    private void assertBuiltIndexIsNotNull() {
        assertNotNull(builtIndexes);
    }
        
    @Test
    public void testAllIndexesWereBuilt() {        
        constructBuilder();
        doBuildIndex();
        assertNumberOfIndexesBuiltIs(currentTestSample.getTotalIndexes());        
    }
    
    private void assertNumberOfIndexesBuiltIs(int expectedValue) {
        assertEquals(expectedValue, builtIndexes.size());
    }
    
    @Test
    public void testBuiltIndexesContainSampleIndex() {
        constructBuilder();
        doBuildIndex();
        assertIndexesContainSampleIndexString();
    }

    private void assertIndexesContainSampleIndexString() {
        boolean contained = builtIndexes.contains(currentTestSample.getSampleIndexString());
        assertTrue(contained);
    }
   
}
