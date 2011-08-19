package com.megadict.format.dict.index.segment;

import static org.junit.Assert.*;

import java.util.*;
import org.junit.*;

import com.megadict.format.dict.index.segment.Segment;
import com.megadict.format.dict.index.segment.SegmentStore;

public class SegmentStoreTest {

    @SuppressWarnings("unused")
    private static final Segment[] splittingSegments = { 
            new Segment("00-database-info", "7-bit ASCII code"),
            new Segment("7-bit ASCII code set", "a la mode"), 
            new Segment("a posteriori", "abacterial"),
            new Segment("abaction", "abase"), 
            new Segment("abasement", "abbey"), 
            new Segment("abbo", "abdicator"),
            new Segment("abdomen", "aberrancy"), 
            new Segment("aberrant", "abide"),
            new Segment("anglicism", "angonekton"), 
            new Segment("angora", "angular modulation"),
            new Segment("Wage theory", "wardrobe dealer"),

    };
    
    private static final Segment[] nonSplittingSegments = {
        new Segment("00-database-info", "7-bit ASCII code"),
        new Segment("7-bit ASCII code", "a la mode"), 
        new Segment("a la mode", "abacterial"),
        new Segment("abacterial", "abase"), 
        new Segment("abase", "abbey"), 
        new Segment("abbey", "abdicator"),
        new Segment("abdicator", "aberrancy"), 
        new Segment("aberrancy", "abide"),
        new Segment("abide", "angonekton"), 
        new Segment("angonekton", "angular modulation"),
        new Segment("angular modulation", "wardrobe dealer"),
    };

    @Test
    public void testGetSegmentContains() {
        SegmentStore store = new SegmentStore(Arrays.asList(nonSplittingSegments));

        String testWord = "00-database-info";
        Segment expectedSegment = new Segment("00-database-info", "7-bit ASCII code");
        Segment foundSegment = store.findSegmentPossiblyContains(testWord);

        assertEquals(expectedSegment, foundSegment);
    }
}
