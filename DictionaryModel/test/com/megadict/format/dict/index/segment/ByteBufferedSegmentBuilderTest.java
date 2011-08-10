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
        
        Segment segment = segments.get(10);        
        System.out.println(segment);
    }

}
