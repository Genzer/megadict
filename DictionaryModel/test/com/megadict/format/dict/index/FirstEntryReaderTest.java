package com.megadict.format.dict.index;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;

import org.junit.Test;

import com.megadict.format.dict.parser.IndexParser;
import com.megadict.format.dict.parser.IndexParsers;

public class FirstEntryReaderTest {

    @Test
    public void testRead() {

        final String givenContent = "00-database-info\tA\tNl\n00-database-short" +
                "\tNl\t5\n00-database-url\tOe\tBD\n1 to 1 relationship\toN3u\t2";

        final String givenFirstIndexString = "00-database-info\tA\tNl";

        ByteArrayInputStream testInputData = new ByteArrayInputStream(givenContent.getBytes());

        Index expected = makeIndex(givenFirstIndexString);

        Index actual = FirstEntryReader.read(testInputData);

        assertEquals(expected, actual);
    }

    private Index makeIndex(String indexString) {
        IndexParser parser = IndexParsers.newParser();
        return parser.parse(indexString);
    }

}
