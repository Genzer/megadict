package com.megadict.format.dict.index.segment;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

public class ByteArrayInnerBufferTest {

    private static final String GIVEN_EXPECTED_CLEANED_INPUT = "z\tr\nknobbed\twel9\tU\nknobble\tTLpO\t5\n"
            + "knobbly\tTLqH\tCa\nknobby\twemR\t+\nknobkerrie\twenP\tCA\nknobstick\tTLsh\t"
            + "CL\nknock\tTLus\t1F\nknock-about\tTMjx\tHN\nknock-down\tTMq+\tDg";

    private static final String GIVEN_LEFT_OVER_FROM_INPUT = "\nknockdown\twetp";
    private static final String GIVEN_PREVIOUS_LEFT_OVER = "knob\t3aI";
    private static final String GIVEN_UNCLEANED_INPUT = GIVEN_EXPECTED_CLEANED_INPUT + GIVEN_LEFT_OVER_FROM_INPUT;

    @Test
    public void testCleanLeftOver() {

        givenInputs(GIVEN_UNCLEANED_INPUT.getBytes(), GIVEN_EXPECTED_CLEANED_INPUT.getBytes());
        givenLeftOvers(GIVEN_LEFT_OVER_FROM_INPUT.getBytes(), GIVEN_PREVIOUS_LEFT_OVER.getBytes());

        testBuffer = makeBufferFromGivenInput();
        final String expectedOuput = explectedAfterCleaningOutputBuffer();

        testBuffer.cleanLeftOverIfAny();

        String expected = new String(expectedOuput);
        String actual = new String(testBuffer.outputBuffer);
        assertEquals(expected, actual);
    }

    private void givenInputs(byte[] inputBuffer, byte[] expectedCleanInput) {
        this.givenInputBuffer = inputBuffer;
        this.givenExpectedCleanInput = expectedCleanInput;
    }

    private void givenLeftOvers(byte[] currentLeftOver, byte[] previousLeftOver) {
        this.givenCurrentLeftOver = currentLeftOver;
        this.givenPreviousLeftOver = previousLeftOver;
    }

    private ByteArrayInnerBuffer makeBufferFromGivenInput() {
        int testBufferSize = this.givenInputBuffer.length;
        ByteArrayInnerBuffer buffer = new ByteArrayInnerBuffer(testBufferSize);
        System.arraycopy(this.givenInputBuffer, 0, buffer.inputBuffer, 0, testBufferSize);
        return buffer;
    }

    private String explectedAfterCleaningOutputBuffer() {
        int outputBufferRemainingLength = computeOutputBufferLengthRemainsAfterCleaning();

        String padding = createPadding(outputBufferRemainingLength);
        
        StringBuilder expectedContent = new StringBuilder(padding.length() + this.givenExpectedCleanInput.length)
                .append(new String(padding)).append(new String(givenExpectedCleanInput));
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
        byte[] padding = new byte[length];
        Arrays.fill(padding, (byte) 0);
        return new String(padding);
    }

    @Test
    public void testAppendPreviousLeftOverIfAny() {

        givenInputs(GIVEN_UNCLEANED_INPUT.getBytes(), GIVEN_EXPECTED_CLEANED_INPUT.getBytes());
        givenLeftOvers(GIVEN_LEFT_OVER_FROM_INPUT.getBytes(), GIVEN_PREVIOUS_LEFT_OVER.getBytes());

        testBuffer = makeBufferAndSetPreviousLeftOver();

        testBuffer.cleanLeftOverIfAny();
        testBuffer.appendPreviousLeftOverIfAny();

        String expected = expectedOutputBufferAfterAppending(testBuffer.outputBuffer);

        byte[] actualOutput = testBuffer.outputBuffer;
        String actual = new String(actualOutput);
        assertEquals(expected, actual);
    }

    private ByteArrayInnerBuffer makeBufferAndSetPreviousLeftOver() {
        ByteArrayInnerBuffer buffer = makeBufferFromGivenInput();

        buffer.previousLeftOver = new byte[givenPreviousLeftOver.length];

        System.arraycopy(givenPreviousLeftOver, 0, buffer.previousLeftOver, 0, givenPreviousLeftOver.length);

        return buffer;
    }

    private String expectedOutputBufferAfterAppending(byte[] outputBuffer) {
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

        givenInputs(GIVEN_UNCLEANED_INPUT.getBytes(), GIVEN_EXPECTED_CLEANED_INPUT.getBytes());
        givenLeftOvers(GIVEN_LEFT_OVER_FROM_INPUT.getBytes(), GIVEN_PREVIOUS_LEFT_OVER.getBytes());

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

        givenInputs(GIVEN_UNCLEANED_INPUT.getBytes(), GIVEN_EXPECTED_CLEANED_INPUT.getBytes());
        givenLeftOvers(GIVEN_LEFT_OVER_FROM_INPUT.getBytes(), GIVEN_PREVIOUS_LEFT_OVER.getBytes());

        testBuffer = makeBufferAndSetPreviousLeftOver();

        String expectedFirstWord = "knob";

        testBuffer.clean();
        String actualFirstWord = testBuffer.fistWord();

        assertEquals(expectedFirstWord, actualFirstWord);
    }

    @Test
    public void testGetLastHeadWord() {

        givenInputs(GIVEN_UNCLEANED_INPUT.getBytes(), GIVEN_EXPECTED_CLEANED_INPUT.getBytes());
        givenLeftOvers(GIVEN_LEFT_OVER_FROM_INPUT.getBytes(), GIVEN_PREVIOUS_LEFT_OVER.getBytes());

        testBuffer = makeBufferAndSetPreviousLeftOver();

        String expectedLastWord = "knock-down";

        testBuffer.clean();
        String actualLastWord = testBuffer.lastWord();

        assertEquals(expectedLastWord, actualLastWord);
    }

    private ByteArrayInnerBuffer testBuffer;
    private byte[] givenExpectedCleanInput;
    private byte[] givenInputBuffer;
    private byte[] givenPreviousLeftOver;
    private byte[] givenCurrentLeftOver;
}
