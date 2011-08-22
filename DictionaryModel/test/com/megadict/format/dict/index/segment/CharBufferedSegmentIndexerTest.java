package com.megadict.format.dict.index.segment;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.util.List;

import org.junit.Test;

public class CharBufferedSegmentIndexerTest {

    @Test
    public void testBuild() {
        File indexFile = new File("C:/test/av.index");

        SegmentBuilder builder = new CharBufferedSegmentBuilder(indexFile);
        builder.build();

        List<Segment> segments = builder.builtSegments();
        assertFalse(segments.isEmpty());

        System.out.println("Num of Segments: " + segments.size());

        printSegmentAt(segments, 0);
        printSegmentAt(segments, 1);
        printSegmentAt(segments, segments.size() - 1);
    }

    private void printSegmentAt(List<Segment> segments, int index) {
        Segment segment = segments.get(index);
        System.out.println(segment);
        System.out.println();
    }
}
