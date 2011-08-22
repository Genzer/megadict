package com.megadict.format.dict.index.segment;

import java.io.File;
import java.util.*;

import org.junit.*;

public class SegmentIndexWriterTest {

    @Test
    public void testWriteWithHumanReadableFormat() {
        
        File indexFile = new File("C:/test/av.index");
        Collection<Segment> segments = buildSegments(indexFile);
        
        File unsortedFile = new File("C:/test/hndSegmentUnsortedList.txt");
        writeSegmentsToFile(unsortedFile, segments);

        Collection<Segment> sortedSegments = sortSegments(segments);
        
        File outputFile = new File("C:/test/hndSegmentSortedList.txt");
        writeSegmentsToFile(outputFile, sortedSegments);
    }

    private Collection<Segment> buildSegments(File indexFile) {
        SegmentBuilder builder = new CharBufferedSegmentBuilder(indexFile);
        builder.build();
        return builder.builtSegments();
    }
    
    private void writeSegmentsToFile(File outputFile, Collection<Segment> segments) {
        SegmentIndexWriter unsortedWriter = new SegmentIndexWriter(outputFile, segments);
        unsortedWriter.write(new HumanReadableWritingStrategy());
    }
    
    private Collection<Segment> sortSegments(Collection<Segment> unsorted) {
        TreeMap<String, Segment> sortedMap = new TreeMap<String, Segment>(String.CASE_INSENSITIVE_ORDER);
        
        for (Segment segment : unsorted) {
            sortedMap.put(segment.upperbound(), segment);
        }
        
        return sortedMap.values();
    }
}
