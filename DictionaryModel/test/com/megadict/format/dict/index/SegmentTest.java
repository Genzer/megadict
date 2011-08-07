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
    
    @Test
    public void testEqualsReflectivity() {
        Segment a = new Segment("absolute", "awake");
        assertTrue(a.equals(a));
    }
    
    @Test
    public void testEqualsSymmetry() {
        Segment a = new Segment("absolute", "awake");
        Segment b = new Segment("absolute", "awake");
        assertTrue(a.equals(b));
        assertTrue(b.equals(a));
    }
    
    @Test
    public void testEqualsNullity() {
        Segment a = new Segment("a", "b");
        assertFalse(a.equals(null));
    }
    
    @Test
    public void testEqualsWithAnotherClass() {
        Segment a = new Segment("a", "b");
        Object b = new Object();
        assertFalse(a.equals(b));
    }
    
    @Test
    public void testEqualsWithNonEquals() {
        Segment a = new Segment("a", "b");
        Segment b = new Segment("a", "c");
        assertFalse(a.equals(b));
        assertFalse(b.equals(a));
    }

}
