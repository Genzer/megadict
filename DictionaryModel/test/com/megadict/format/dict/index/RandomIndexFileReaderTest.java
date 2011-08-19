package com.megadict.format.dict.index;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.megadict.format.dict.index.segment.ByteBufferedSegmentIndexer;
import com.megadict.format.dict.index.segment.Segment;
import com.megadict.format.dict.index.segment.SegmentBuilder;
import com.megadict.format.dict.index.segment.SegmentStore;

public class RandomIndexFileReaderTest {

    @Test
    public void testGetIndexOf() {
        File hndIndexFile = new File("C:/test/av.index");
        
        SegmentBuilder builder = new ByteBufferedSegmentIndexer(hndIndexFile);
        builder.build();        
        List<Segment> segments = builder.builtSegments();
        
        SegmentStore store = new SegmentStore(segments);
        
        String testWord = "zillion";
        Segment segment = store.findSegmentPossiblyContains(testWord);
        assertNotNull(segment);
        System.out.println(segment);
        
        RandomIndexFileReader reader = new RandomIndexFileReader(hndIndexFile);
        Index index = reader.getIndexOf(testWord, segment);
        assertNotNull(index);
        System.out.println(index);
    }

}
