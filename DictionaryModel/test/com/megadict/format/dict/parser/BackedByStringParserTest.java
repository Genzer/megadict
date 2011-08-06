package com.megadict.format.dict.parser;

import static org.junit.Assert.*;
import org.junit.*;
import org.junit.rules.ExpectedException;

import com.megadict.exception.ParseIndexException;
import com.megadict.format.dict.index.Index;

public class BackedByStringParserTest {

    @Rule
    public ExpectedException expectedToThrow = ExpectedException.none();

    @Test
    public void testParseValidString() {
        String testIndexString = "00-database-info\tA\tNl";
        Index expectedIndex = new Index("00-database-info", 0, 869);

        Index actualIndex = new BackedByStringParser().parse(testIndexString);
        assertEquals(expectedIndex, actualIndex);
    }

    @Test
    public void testParseInvalidString() {
        String invalidFormat = "234234 23423\t23dd\t24 \t";

        expectedToThrow.expect(ParseIndexException.class);
        expectedToThrow.expectMessage("The input string is in invalid format: \"234234 23423\t23dd\t24 \t\".");

        new BackedByStringParser().parse(invalidFormat);
    }

    @Test
    public void testParseStringWithInvalidBase64Encoding() {
        String invalidString = "invalid\t234?\t234";

        expectedToThrow.expect(ParseIndexException.class);
        expectedToThrow.expectMessage("The input string is in invalid format: \"invalid\t234?\t234\".");

        new BackedByStringParser().parse(invalidString);
    }
}
