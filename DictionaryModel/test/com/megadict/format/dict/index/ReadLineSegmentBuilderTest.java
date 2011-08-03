package com.megadict.format.dict.index;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.junit.Test;

public class ReadLineSegmentBuilderTest {

    @Test
    public void testBuild() {
       File file = new File("C:/test/av.index");
       
       ReadLineSegmentBuilder builder = new ReadLineSegmentBuilder(file);
       
       builder.build();
       
       List<Segment> segments = builder.builtSegments();
       
       assertFalse(segments.isEmpty());
       
       Segment segment = segments.get(10);
       
       System.out.println(segment.lowerbound());
    }

}
