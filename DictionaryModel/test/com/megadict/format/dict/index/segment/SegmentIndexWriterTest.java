package com.megadict.format.dict.index.segment;

import java.io.File;
import java.util.*;

import org.junit.Test;

public class SegmentIndexWriterTest {

    @Test
    public void testWriteWithHumanReadableFormat() {
        File indexFile = new File("C:/test/av.index");
        
        SegmentBuilder builder = new ByteBufferedSegmentIndexer(indexFile);
        
        builder.build();
        
        List<Segment> segments = builder.builtSegments();
        
        TreeMap<String, Segment> sortedMap = new TreeMap<String, Segment>(String.CASE_INSENSITIVE_ORDER);
        
        for (Segment segment : segments) {
            sortedMap.put(segment.upperbound(), segment);
        }
        
        Collection<Segment> sortedSegments = sortedMap.values();
        File outputFile = new File("C:/test/hndSegmentSortedList.txt");
        SegmentIndexWriter writer = new SegmentIndexWriter(outputFile, sortedSegments);
        writer.write(new HumanReadableWritingStrategy());
    }

}
