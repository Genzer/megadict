package com.megadict.format.dict.index;

import static org.junit.Assert.*;
import org.junit.Test;

public class BufferUtilTest {

    private static final String content = "A shares\tppv+\tBd\nA4-size\toOVN\t0\naa\tsnur\tBX\naaa\tsnw"
            + "C\tCE\naardvark\tsnyG\t1\naard-wolf\t5k\tBZ\naardwolf\tsny7\t9\naasvogel\t"
            + "69\tBX\nab\tsnz4\tB2\naba\t8U\tBd\nabac, abacus\t2GQg\t7\nabaca\tsn1u\tBc\n"
            + "abaci\t9x\tDb\nabacist\tsn3K\tBG\naback\tBBM\tFp\nabacterial\tsn4Q\t4\nab"
            + "action\tsn5I\tBH\nabactus\tsn6P\tp\nabacus\tBG1\tDc\nabacus\toOWB\tc\nabaddo"
            + "n\tBKR\tBI\nabaft\tBLZ\tDH\nabalienate\tsn64\tBA\nabalienation\tsn74\t8\nabal"
            + "one\tBOg\tBK\nabampere\toOWd\tCC\nabandon\tBPq\tGn\nabandon\toOYf\tn\nabandon "
            + "call\toOZG\ti\nabandoned\tBWR\tBr\nabandoner\tBX8\tBL\nabandonment\tBZH\tDi\nab"
            + "andonment\toOZo\tq\nabapikal\tsn80\tBM\nabarticular\tsn+A\tBP\nabase\tBcp\tCM\naba"
            + "sement\tBe1\tB1\nabash\tBgq\tBm\nabashed\tsn/P\tw\nabashment\tBiQ\tBk\nabask\tBj0\tB"
            + "z\nabatable\tBln\tEU\nabate\tBp7\tH4\nabatement";
    
    @Test
    public void testGetFirstWordInBlock() {
        String expected = "A shares";
        String actual = BufferUtil.firstWordInBlock(content.getBytes());
        assertEquals(expected, actual);
    }
    
    
    @Test
    public void testGetFirstWordInBlockWithEmptyInput() {
        String actual = BufferUtil.firstWordInBlock(new byte[0]);
        assertEquals("", actual);
    }

    
    @Test
    public void testGetFirstWordInBlockWithShortInput() {
        String shortInput = "ds8 23324 tra";
        String actual = BufferUtil.firstWordInBlock(shortInput.getBytes());
        assertEquals("", actual);
    }
    
    
    @Test
    public void testGetLastWordInBlock() {
        String expected = "abate";        
        String actual = BufferUtil.lastWordInBlock(content.getBytes());        
        assertEquals(expected, actual);
    }
}
