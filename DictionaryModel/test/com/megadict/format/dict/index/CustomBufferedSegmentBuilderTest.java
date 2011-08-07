package com.megadict.format.dict.index;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.junit.Test;

public class CustomBufferedSegmentBuilderTest {

    @Test
    public void testBuild() {
        File indexFile = new File("C:/test/av.index");
        
        SegmentBuilder builder = new CustomBufferedSegmentBuilder(indexFile);
        
        builder.build();
        
        List<Segment> segments = builder.builtSegments();
        
        assertFalse(segments.isEmpty());
        
        Segment segment = segments.get(10);
        
        System.out.println(segment);
    }

}
