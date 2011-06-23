package format.dict;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import exception.ResourceMissingException;

public class IndexBuilderTest {
    
    private static class IndexFileSample {
        
        public IndexFileSample(String path,
                    int totalIndexes, String sampleIndexString) {
            this.filePath = path;
            this.totalIndexes = totalIndexes;
            this.sampleIndexString = sampleIndexString;
        }
        
        public final String filePath;
        public final int totalIndexes;
        public final String sampleIndexString;
        
    }
    
    private IndexFileSample currentTestSample;
    private IndexBuilder testBuilder;    
    private Set<Index> builtIndexes; 
    
    @Before
    public void initialize() {
        //currentTestSample = makePackageIncludedSample();
        currentTestSample = makeEnglishVietnameseSample();
    }
        
    private IndexFileSample makePackageIncludedSample() {
        String filePath = 
            IndexBuilderTest.class.getResource("/format/dict/sampleIndexFile.index").getFile();
        int numOfIndexes = 100;
        String sampleIndexString = "abaci\t9x\tDb";
        
        return new IndexFileSample(filePath, numOfIndexes, sampleIndexString);
    }
    
    private IndexFileSample makeThesaurusIndexFileSample() {
        String filePath = "C:\\test\\dictd\\moby-thesaurus.index";
        int numOfIndexes = 30263;
        String sampleIndexString = "00-database-short\tBZ\t8";
        
        return new IndexFileSample(filePath, numOfIndexes, sampleIndexString);
    }
    
    private IndexFileSample makeEnglishVietnameseSample() {
        String filePath = "C:\\test\\av.index";
        int numOfIndexes = 108857;
        String sampleIndexString = "Z-score\tsmpF\tm";
        
        return new IndexFileSample(filePath, numOfIndexes, sampleIndexString);
    }
    
    @Test (expected = ResourceMissingException.class)
    public void testNotFoundIndexFile() throws ResourceMissingException {
        constructBuilderWithPath("this is a crap string");
    }
    
    private void constructBuilderWithPath(String file) {
        this.testBuilder = new IndexBuilder(file);
    }
    
    @Test
    public void testBuildIndexFile() {
        constructBuilder();
        doBuildIndex();
        assertBuiltIndexIsNotNull();
    }
    
    private void constructBuilder() {
        constructBuilderWithPath(currentTestSample.filePath);
    }
    
    private void doBuildIndex() {
        builtIndexes = testBuilder.build();      
    }
    
    private void assertBuiltIndexIsNotNull() {
        assertNotNull(builtIndexes);
    }
        
    @Test
    public void testAllIndexesWereBuilt() {        
        constructBuilder();
        doBuildIndex();
        assertNumberOfIndexesBuiltIs(currentTestSample.totalIndexes);        
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
        Index parsedIndex = parser.parse(currentTestSample.sampleIndexString);
        return parsedIndex;
    }
    
    private void assertBuiltIndexesContains(Index index) {
        boolean contained = builtIndexes.contains(index);
        assertTrue(contained);
    }
   
}
