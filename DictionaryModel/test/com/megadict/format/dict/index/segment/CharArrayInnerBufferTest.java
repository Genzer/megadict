package com.megadict.format.dict.index.segment;

import static org.junit.Assert.*;
import org.junit.*;
import java.util.Arrays;

public class CharArrayInnerBufferTest {

    private static final String GIVEN_EXPECTED_CLEANED_INPUT = "z\tr\nknobbed\twel9\tU\nknobble\tTLpO\t5\n"
            + "knobbly\tTLqH\tCa\nknobby\twemR\t+\nknobkerrie\twenP\tCA\nknobstick\tTLsh\t"
            + "CL\nknock\tTLus\t1F\nknock-about\tTMjx\tHN\nknock-down\tTMq+\tDg";

    private static final String GIVEN_LEFT_OVER_FROM_INPUT = "\nknockdown\twetp";
    private static final String GIVEN_PREVIOUS_LEFT_OVER = "knob\t3aI";
    private static final String GIVEN_UNCLEANED_INPUT = GIVEN_EXPECTED_CLEANED_INPUT + GIVEN_LEFT_OVER_FROM_INPUT;

    @Test
    public void testCleanLeftOver() {
        
        givenInputs(GIVEN_UNCLEANED_INPUT.toCharArray(), GIVEN_EXPECTED_CLEANED_INPUT.toCharArray());
        givenLeftOvers(GIVEN_LEFT_OVER_FROM_INPUT.toCharArray(), GIVEN_PREVIOUS_LEFT_OVER.toCharArray());
        
        testBuffer = makeBufferFromGivenInput();
        final String expectedOuput = explectedAfterCleaningOutputBuffer();

        testBuffer.cleanLeftOverIfAny();

        String expected = new String(expectedOuput);
        String actual = new String(testBuffer.outputBuffer);
        assertEquals(expected, actual);
    }
    
    private void givenInputs(char[] inputBuffer, char[] expectedCleanInput) {
        this.givenInputBuffer = inputBuffer;
        this.givenExpectedCleanInput = expectedCleanInput;
    }
    
    private void givenLeftOvers(char[] currentLeftOver, char[] previousLeftOver) {
        this.givenCurrentLeftOver = currentLeftOver;
        this.givenPreviousLeftOver = previousLeftOver;
    }

    private CharArrayBufferForSegmentBuilding makeBufferFromGivenInput() {
        int testBufferSize = this.givenInputBuffer.length;
        CharArrayBufferForSegmentBuilding buffer = new CharArrayBufferForSegmentBuilding(testBufferSize);
        System.arraycopy(this.givenInputBuffer, 0, buffer.inputBuffer, 0, testBufferSize);
        return buffer;
    }

    private String explectedAfterCleaningOutputBuffer() {
        int outputBufferRemainingLength = computeOutputBufferLengthRemainsAfterCleaning();
        
        String padding = createPadding(outputBufferRemainingLength);
        StringBuilder expectedContent = 
                new StringBuilder(padding.length() + this.givenExpectedCleanInput.length)
                    .append(padding).append(this.givenExpectedCleanInput);
        return expectedContent.toString();
    }

    private int computeOutputBufferLengthRemainsAfterCleaning() {
        int leftOverLength = this.givenCurrentLeftOver.length;
        int inputLengthAfterClean = this.givenInputBuffer.length - leftOverLength;
        int outputBufferLength = outputBufferLength();
        return outputBufferLength - inputLengthAfterClean;
    }
    
    private int outputBufferLength() {
        return testBuffer.outputBuffer.length;
    }

    private static String createPadding(int length) {
        char[] padding = new char[length];
        Arrays.fill(padding, '\0');
        return new String(padding);
    }

    @Test
    public void testAppendPreviousLeftOverIfAny() {
        
        givenInputs(GIVEN_UNCLEANED_INPUT.toCharArray(), GIVEN_EXPECTED_CLEANED_INPUT.toCharArray());
        givenLeftOvers(GIVEN_LEFT_OVER_FROM_INPUT.toCharArray(), GIVEN_PREVIOUS_LEFT_OVER.toCharArray());
        
        testBuffer = makeBufferAndSetPreviousLeftOver();

        testBuffer.cleanLeftOverIfAny();
        testBuffer.appendPreviousLeftOverIfAny();

        String expected = expectedOutputBufferAfterAppending(testBuffer.outputBuffer);

        char[] actualOutput = testBuffer.outputBuffer;
        String actual = new String(actualOutput);
        assertEquals(expected, actual);
    }

    private CharArrayBufferForSegmentBuilding makeBufferAndSetPreviousLeftOver() {
        CharArrayBufferForSegmentBuilding buffer = makeBufferFromGivenInput();

        buffer.previousLeftOver = new char[givenPreviousLeftOver.length];

        System.arraycopy(givenPreviousLeftOver, 0, buffer.previousLeftOver, 0,
                givenPreviousLeftOver.length);

        return buffer;
    }

    private String expectedOutputBufferAfterAppending(char[] outputBuffer) {
        int spacesRemainsAfferAppend = expectedOutputBufferLengthRemainsAfterAppending();
        String padding = createPadding(spacesRemainsAfferAppend);
        String expected = padding + GIVEN_PREVIOUS_LEFT_OVER + GIVEN_EXPECTED_CLEANED_INPUT;
        return expected;
    }

    private int expectedOutputBufferLengthRemainsAfterAppending() {
        int spacesRemainsAfterClean = computeOutputBufferLengthRemainsAfterCleaning();
        int spacesRemainsAfferAppend = spacesRemainsAfterClean - givenPreviousLeftOver.length;
        return spacesRemainsAfferAppend;
    }

    @Test
    public void testStartPositionToWrite() {
        
        givenInputs(GIVEN_UNCLEANED_INPUT.toCharArray(), GIVEN_EXPECTED_CLEANED_INPUT.toCharArray());
        givenLeftOvers(GIVEN_LEFT_OVER_FROM_INPUT.toCharArray(), GIVEN_PREVIOUS_LEFT_OVER.toCharArray());
        
        testBuffer = makeBufferAndSetPreviousLeftOver();

        testBuffer.cleanLeftOverIfAny();
        testBuffer.appendPreviousLeftOverIfAny();
        testBuffer.computeOffsetToPrepareWriting();

        int expectedPosition = expectedOutputBufferLengthRemainsAfterAppending();
        int actualPosition = testBuffer.startPositionToWrite();
        assertEquals(expectedPosition, actualPosition);
    }

    @Test
    public void testGetFirstHeadWord() {
        
        givenInputs(GIVEN_UNCLEANED_INPUT.toCharArray(), GIVEN_EXPECTED_CLEANED_INPUT.toCharArray());
        givenLeftOvers(GIVEN_LEFT_OVER_FROM_INPUT.toCharArray(), GIVEN_PREVIOUS_LEFT_OVER.toCharArray());
        
        testBuffer = makeBufferAndSetPreviousLeftOver();
       
        String expectedFirstWord = "knob";

        testBuffer.clean();
        String actualFirstWord = testBuffer.fistWord();

        assertEquals(expectedFirstWord, actualFirstWord);
    }

    @Test
    public void testGetLastHeadWord() {
        
        givenInputs(GIVEN_UNCLEANED_INPUT.toCharArray(), GIVEN_EXPECTED_CLEANED_INPUT.toCharArray());
        givenLeftOvers(GIVEN_LEFT_OVER_FROM_INPUT.toCharArray(), GIVEN_PREVIOUS_LEFT_OVER.toCharArray());
        
        testBuffer = makeBufferAndSetPreviousLeftOver();
        
        String expectedLastWord = "knock-down";

        testBuffer.clean();
        String actualLastWord = testBuffer.lastWord();

        assertEquals(expectedLastWord, actualLastWord);
    }
    
    private CharArrayBufferForSegmentBuilding testBuffer;
    private char[] givenExpectedCleanInput;
    private char[] givenInputBuffer;
    private char[] givenPreviousLeftOver;
    private char[] givenCurrentLeftOver;
}
