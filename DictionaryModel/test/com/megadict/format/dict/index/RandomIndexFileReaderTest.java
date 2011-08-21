package com.megadict.format.dict.index;

import static org.junit.Assert.assertNotNull;

import java.io.*;
import java.util.*;

import org.junit.*;

import com.megadict.format.dict.index.segment.CharBufferedSegmentIndexer;
import com.megadict.format.dict.index.segment.Segment;
import com.megadict.format.dict.index.segment.SegmentBuilder;
import com.megadict.format.dict.index.segment.SegmentStore;

public class RandomIndexFileReaderTest {

    @Test
    public void testGetIndexOf() {
        File hndIndexFile = new File("C:/test/av.index");

        SegmentBuilder builder = new CharBufferedSegmentIndexer(hndIndexFile);
        builder.build();
        List<Segment> segments = builder.builtSegments();
        
        SegmentStore segmentStore = new SegmentStore(segments);

        String testWord = "zoom";

        Segment segment = segmentStore.findSegmentPossiblyContains(testWord);
        assertNotNull(segment);
        System.out.println(segment);

        RandomIndexFileReader reader = new RandomIndexFileReader(hndIndexFile, segmentStore);
        Index index = reader.getIndexOf(testWord);
        assertNotNull(index);
        System.out.println(index);
    }
    
}
