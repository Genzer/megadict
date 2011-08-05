package com.megadict.format.dict.index;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.junit.Test;

public class SegmentBuilderTest {

    @Test
    public void testBuild() {
        File indexFile = new File("C:/test/av.index");
        
        SegmentBuilder builder = new CustomBufferedSegmentBuilder(indexFile);
        
        builder.build();
        
        List<Segment> builtSegments = builder.builtSegments();
        assertFalse(builtSegments.isEmpty());
        
    }

}
