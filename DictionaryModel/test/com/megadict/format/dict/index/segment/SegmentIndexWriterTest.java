package com.megadict.format.dict.index.segment;

import java.io.File;
import java.text.Collator;
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
        Collator collator = Collator.getInstance(new Locale("vi", "VI"));
        collator.setStrength(Collator.SECONDARY);
        
        TreeMap<String, Segment> sortedMap = new TreeMap<String, Segment>(collator);
        
        for (Segment segment : unsorted) {
            sortedMap.put(segment.upperbound(), segment);
        }
        System.out.println(sortedMap);
        return sortedMap.values();
    }
}
