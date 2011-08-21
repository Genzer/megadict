package com.megadict.format.dict.index.segment;

import static org.junit.Assert.*;

import org.junit.Test;

public class ByteBufferToolTest {

    private static final String givenContent =
            "A shares\tppv+\tBd\nA4-size\t\tw\nabashment\tBiQ\tBk\nabask\tBj0\tB"
                    + "z\nabatable\tBln\tEU\nabate\tBp7\tH4\nabatement\tre34\teA3";

    @Test
    public void testFindFirstNewlineChar() {
        int expected = 16;
        int actual = ByteBufferTool.findFirstNewlineChar(givenContent.getBytes());
        assertEquals(expected, actual);
    }

    @Test
    public void testFindLastNewlinechar() {
        int expected = 86;
        int actual = ByteBufferTool.findLastNewlineChar(givenContent.getBytes());
        assertEquals(expected, actual);
    }

    @Test
    public void testFindForwardFirstCharInRange() {
        char findChar = '\t';
        int expected = 8;

        int start = 0;
        int end = givenContent.length();
        int actual =
                ByteBufferTool.findForwardFirstCharInRange(givenContent.getBytes(), start, end, findChar);

        assertEquals(expected, actual);
    }

    @Test
    public void testFindForwardLastCharInRange() {
        char findChar = 'H';
        int expected = 84;

        int start = givenContent.length() - 1;
        int end = 0;
        int actual =
                ByteBufferTool.findBackwardFirstCharInRange(givenContent.getBytes(), start, end, findChar);

        assertEquals(expected, actual);
    }
}
