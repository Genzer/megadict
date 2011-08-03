package com.megadict.format.dict.index;

import static org.junit.Assert.*;

import org.junit.Test;

public class SegmentTest {

    @Test
    public void testContainsWithValidString() {
        Segment segment = new Segment("absolute", "awake");

        String string = "acc";

        boolean isInRange = segment.contains(string);

        assertTrue(isInRange);
    }
    
    
    @Test
    public void testContainsWithWrongString() {
        Segment segment = new Segment("absolute", "awake");
        
        String string = "ball";
        
        boolean isInRange = segment.contains(string);
        
        assertFalse(isInRange);
    }
    
    
    @Test
    public void testWithNullArgument() {
        Segment segment = new Segment("absolute", "awake");
        
        assertFalse(segment.contains(null));
    }

}
