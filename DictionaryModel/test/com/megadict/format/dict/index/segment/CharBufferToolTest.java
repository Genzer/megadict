package com.megadict.format.dict.index.segment;

import static org.junit.Assert.*;

import org.junit.Test;

public class CharBufferToolTest {

    private static final String givenContent =
        "A shares\tppv+\tBd\nA4-size\t\tw\nabashment\tBiQ\tBk\nabask\tBj0\tB"
                + "z\nabatable\tBln\tEU\nabate\tBp7\tH4\nabatement\tre34\teA3";

    @Test
    public void testFindFirstNewlineChar() {
        int expected = 16;
        int actual = CharBufferTool.findFirstNewlineChar(givenContent.toCharArray());
        assertEquals(expected, actual);
    }

    @Test
    public void testFindLastNewlinechar() {
        int expected = 86;
        int actual = CharBufferTool.findLastNewlineChar(givenContent.toCharArray());
        assertEquals(expected, actual);
    }

    @Test
    public void testFindForwardFirstCharInRange() {
        char findChar = '\t';
        int expected = 8;

        int start = 0;
        int end = givenContent.length();
        int actual =
                CharBufferTool.findForwardFirstCharInRange(givenContent.toCharArray(), start, end, findChar);

        assertEquals(expected, actual);
    }

    @Test
    public void testFindForwardLastCharInRange() {
        char findChar = 'H';
        int expected = 84;

        int start = givenContent.length() - 1;
        int end = 0;
        int actual =
                CharBufferTool.findBackwardFirstCharInRange(givenContent.toCharArray(), start, end, findChar);

        assertEquals(expected, actual);
    }
}
