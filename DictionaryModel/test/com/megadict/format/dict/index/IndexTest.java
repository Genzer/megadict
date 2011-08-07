package com.megadict.format.dict.index;

import static org.junit.Assert.*;

import org.junit.Test;

public class IndexTest {

    @Test
    public void testEqualsReflectivity() {
        Index a = new Index("headword", 123, 456);
        assertTrue(a.equals(a));
    }

    @Test
    public void testEqualsSymmetry() {
        Index a = new Index("headword", 123, 456);
        Index b = new Index("headword", 123, 456);
        
        for (int i = 0; i < 100; i++) {
            assertTrue(a.equals(b));
            assertTrue(b.equals(a));
        }
    }

    @Test
    public void testEqualsWithOtherClass() {
        Index a = new Index("headword", 123, 456);
        String b = "bla bla bla";
        assertFalse(a.equals(b));
    }

    @Test
    public void testEqualsWithNullity() {
        Index a = new Index("headword", 123, 456);
        assertFalse(a.equals(null));
    }

    @Test
    public void testWithNonEqual() {
        Index a = new Index("headword", 123, 456);
        Index b = new Index("headword", 12, 456);
        assertFalse(a.equals(b));
    }

    @Test
    public void testEqualsTransitivity() {
        Index a = new Index("headword", 123, 456);
        Index b = new Index("headword", 123, 456);
        Index c = new Index("headword", 123, 456);
        assertTrue(a.equals(b));
        assertTrue(b.equals(c));
        assertTrue(a.equals(a));
    }

}
