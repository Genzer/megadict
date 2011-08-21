package com.megadict.format.dict.index;

import static org.junit.Assert.assertNotNull;

import java.io.*;

import java.util.*;

import org.junit.*;

import com.megadict.format.dict.index.segment.*;

public class RandomIndexFileReaderTest {

    @Test
    public void testGetIndexOf() {
        File hndIndexFile = new File("C:/test/av.index");

        SegmentBuilder builder = new ByteBufferedSegmentIndexer(hndIndexFile);
        builder.build();
        List<Segment> segments = builder.builtSegments();
        
        SegmentStore segmentStore = new SegmentStore(segments);

        String[] testWords = {
                "zymurgy", "abort", "zoom", "test", "person"
        };

        RandomIndexFileReader reader = new RandomIndexFileReader(hndIndexFile, segmentStore);
        
        for (String testWord : testWords) {
            Index index = reader.getIndexOf(testWord);
            assertNotNull(index);
            System.out.println(index);
        }
    }
    
    @Test
    public void testGetSurroundingIndexes() {
        File hndIndexFile = new File("C:/test/av.index");

        SegmentBuilder builder = new ByteBufferedSegmentIndexer(hndIndexFile);
        builder.build();
        List<Segment> segments = builder.builtSegments();
        
        SegmentStore segmentStore = new SegmentStore(segments);

        RandomIndexFileReader reader = new RandomIndexFileReader(hndIndexFile, segmentStore);
        
        String testWord = "zebra";
        
        Set<Index> indexes = reader.getIndexesSurrounding(testWord);
        
        for (Index index : indexes) {
            System.out.println(index);
        }
    }
    
}
