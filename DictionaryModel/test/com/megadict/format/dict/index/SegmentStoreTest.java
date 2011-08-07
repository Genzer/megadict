package com.megadict.format.dict.index;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class SegmentStoreTest {

    private static final Segment[] segments = { 
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

    @Test
    public void testGetSegmentContains() {
        SegmentStore store = new SegmentStore(Arrays.asList(segments));

        String testWord = "abasement";
        Segment expectedSegment = new Segment("abasement", "abbey");
        Segment foundSegment = store.getSegmentContains(testWord);

        assertEquals(expectedSegment, foundSegment);
    }

}
