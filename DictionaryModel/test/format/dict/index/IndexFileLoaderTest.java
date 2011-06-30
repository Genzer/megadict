package format.dict.index;

import static org.junit.Assert.*;
import org.junit.*;

import java.util.List;

import exception.ResourceMissingException;
import format.dict.index.IndexFileLoader;

import format.dict.sample.index.*;

public class IndexFileLoaderTest {
    
    private IndexFileSample currentTestSample;
    private IndexFileLoader testBuilder;    
    private List<String> builtIndexes; 
    
    @Before
    public void initialize() {
        currentTestSample = new  EngVi();
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
