package com.megadict.format.dict.index.segment;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.megadict.format.dict.index.segment.ByteBufferedSegmentBuilder;
import com.megadict.format.dict.index.segment.Segment;
import com.megadict.format.dict.index.segment.SegmentBuilder;

public class ByteBufferedSegmentBuilderTest {

    @Test
    public void testBuild() {
        File indexFile = new File("C:/test/av.index");
        
        SegmentBuilder builder = new ByteBufferedSegmentBuilder(indexFile);        
        builder.build();
        
        List<Segment> segments = builder.builtSegments();        
        assertFalse(segments.isEmpty());

        System.out.println("Num of Segments: " + segments.size());
        
        printSegmentAt(segments, 0);
        printSegmentAt(segments, segments.size() - 1);
        
        String testWord = "hello";
        
        Segment segment = determineSegment(segments, testWord);
        System.out.println(segment);
    }
    
    private void printSegmentAt(List<Segment> segments, int index) {
        Segment segment = segments.get(index);
        System.out.println(segment);
    }
    
    private Segment determineSegment(List<Segment> segments, String headword) {
        for (Segment segment : segments) {
            if (segment.contains(headword)) {
                return segment;
            }
        }
        return null;
    }

}
