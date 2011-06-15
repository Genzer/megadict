package format.dict;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import exception.ResourceMissingException;

public class IndexBuilderTest {
    
    @Test (expected = ResourceMissingException.class)
    public void testNotFoundIndexFile() throws ResourceMissingException {
        constructBuilderWithPath("this is a crap string");
    }
    
    private void constructBuilderWithPath(String file) {
        if (testBuilder == null) {
            this.testBuilder = new IndexBuilder(file);            
        }
    }
    
    @Test
    public void testBuildIndexFile() {
        constructBuilder();
        doBuildIndex();
        assertBuiltIndexIsNotNull();
    }
    
    private void constructBuilder() {
        constructBuilderWithPath(SAMPLE_INDEX_FILE);
    }
    
    private void doBuildIndex() {
        if (builtIndexes == null) {
            builtIndexes = testBuilder.build();
        }       
    }
    
    private void assertBuiltIndexIsNotNull() {
        assertNotNull(builtIndexes);
    }
        
    @Test
    public void testAllIndexesWereBuilt() {        
        constructBuilder();
        doBuildIndex();
        assertNumberOfIndexesBuiltIs(NUM_OF_SAMPLE_INDEXES);        
    }
    
    private void assertNumberOfIndexesBuiltIs(int expectedValue) {
        assertEquals(expectedValue, builtIndexes.size());
    }
    
    @Test
    public void testBuiltIndexesContainSampleIndex() {
        constructBuilder();
        doBuildIndex();
        
        Index sampleIndex = createSampleIndex();
        assertBuiltIndexesContains(sampleIndex);
    }
    
    private Index createSampleIndex() {
        IndexParser parser = new IndexTabDilimeterParser();
        Index parsedIndex = parser.parse(SAMPLE_INDEX_STRING);
        return parsedIndex;
    }
    
    private void assertBuiltIndexesContains(Index index) {
        boolean contained = builtIndexes.contains(index);
        assertTrue(contained);
    }
    
    private static final String SAMPLE_INDEX_FILE = 
            IndexBuilderTest.class.getResource("/format/dict/sampleIndexFile.index").getFile();
    
    private static final int NUM_OF_SAMPLE_INDEXES = 100;    
    private static final String SAMPLE_INDEX_STRING = "abaci\t9x\tDb";
    
    private IndexBuilder testBuilder;
    
    private Set<Index> builtIndexes;    
}
