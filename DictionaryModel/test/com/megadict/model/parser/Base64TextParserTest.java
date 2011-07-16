package com.megadict.model.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class Base64TextParserTest {

    @Test(expected = NullPointerException.class)
    public void testNullString() throws NullPointerException {
        Base64TextParser.parse(null);
    }

    @Test
    public void testCheckBase64Text() {
        String[] testStrings = { "Eo", "O324B", "s2Df4" };

        for (String string : testStrings) {
            boolean result = Base64TextParser.isBase64Encoded(string);
            assertTrue(result);
        }
    }

    @Test
    public void testCheckNotBase64Text() {
       String[] testStrings = {
         "normal text", "234S+324?", "As8f823i\\a\tad]"      
       };
       
       for (String string : testStrings) {
           boolean result = Base64TextParser.isBase64Encoded(string);
           assertFalse(result);
       }
    }

    @Test
    public void testParsingValidBase64Text() {
         
         String[] VALID_ENCODED = {
                 "Eo", "A", "tE35", "x06V"
         };
         
         int[] VALID_DECODED = {
                 296, 0, 11816441, 13061781
         };
        
         for (int numCase = 0; numCase < VALID_ENCODED.length; numCase++) {
             int decodedInt = Base64TextParser.parse(VALID_ENCODED[numCase]);
             assertEquals(VALID_DECODED[numCase], decodedInt);
         }
    }
    
    @Test
    public void testParsingByteArray() {
        String valid = "tE35";
        int expected = 11816441;
        
        int actual = Base64TextParser.toInt(valid.getBytes());
        
        assertEquals(expected, actual);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testParsingNotBase64Text() throws IllegalArgumentException {
        String invalidCase = "sa s?3 3400=+";
        Base64TextParser.parse(invalidCase);
    }
}
