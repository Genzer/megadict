package com.megadict.format.dict.index.segment;

public class ByteBufferTool {

    private static enum SearchDirection {
        FORWARD(1) {
            public boolean isOver(int current, int end) {
                return current < end;
            }
        },

        BACKWARD(-1) {
            public boolean isOver(int current, int end) {
                return current >= end;
            }
        };

        SearchDirection(int incrementValue) {
            this.incrementValue = incrementValue;
        }

        public int next(int current) {
            return current += incrementValue;
        }

        public abstract boolean isOver(int current, int end);

        private final int incrementValue;
    }

    private ByteBufferTool() {
    }

    public static String firstHeadWordIn(byte[] buffer) {
        return (buffer.length == 0) ? "" : findAndExtractFirstHeadWord(buffer);
    }

    private static String findAndExtractFirstHeadWord(byte[] buffer) {
        int maxCharsToFind = 300;
        int firstTabChar = findFirstTabCharInBeginningChars(maxCharsToFind, buffer);
        int lastNullChar = findBackwardFirstNullCharFromTabChar(firstTabChar, buffer);

        int beginCopyPos = lastNullChar + 1;
        byte[] firstWordInBytes = wasNotFound(firstTabChar) ? EMPTY_CONTENT : copyOfRange(buffer, beginCopyPos,
                firstTabChar);

        return new String(firstWordInBytes);
    }

    private static boolean wasNotFound(int returnedPosition) {
        return returnedPosition == -1;
    }

    private static int findFirstTabCharInBeginningChars(int numOfChars, byte[] content) {
        int start = 0;
        int end = Math.min(numOfChars, content.length);
        char tabChar = '\t';

        return findFowardFirstOccurrenceOfCharInRange(content, start, end, tabChar);
    }

    private static int findBackwardFirstNullCharFromTabChar(int tabCharPos, byte[] content) {
        return findBackwardFirstOccurrenceOfCharInRange(content, tabCharPos, 0, '\0');
    }

    public static String lastHeadWordIn(byte[] buffer) {
        return (buffer.length == 0) ? "" : findAndExtractLastHeadWord(buffer);
    }

    private static String findAndExtractLastHeadWord(byte[] buffer) {
        int newLineChar = findLastNewlineChar(buffer);
        byte[] fullContent = wasNotFound(newLineChar) ? EMPTY_CONTENT : copyOfRange(buffer, newLineChar + 1,
                buffer.length);

        int firstTabChar = findFirstTabCharInBeginningChars(fullContent.length, fullContent);
        byte[] headWord = wasNotFound(firstTabChar) ? EMPTY_CONTENT : copyOfRange(fullContent, 0, firstTabChar);

        return new String(headWord);
    }

    private static int findLastNewlineChar(byte[] byteArray) {
        int start = byteArray.length - 1;
        int end = 0;
        char newlineChar = '\n';

        return findBackwardFirstOccurrenceOfCharInRange(byteArray, start, end, newlineChar);
    }

    private static int findFowardFirstOccurrenceOfCharInRange(byte[] searchArray, int start, int end, char charToFind) {
        return findFirstOccurrenceOfCharInRange(searchArray, start, end, charToFind, SearchDirection.FORWARD);
    }

    private static int findBackwardFirstOccurrenceOfCharInRange(byte[] searchArray, int start, int end, char charToFind) {
        return findFirstOccurrenceOfCharInRange(searchArray, start, end, charToFind, SearchDirection.BACKWARD);
    }

    private static int findFirstOccurrenceOfCharInRange(byte[] searchArray, int start, int end, char charToFind,
            SearchDirection search) {

        for (int i = start; search.isOver(i, end); i = search.next(i)) {
            if (searchArray[i] == (byte) charToFind) {
                return i;
            }
        }
        return -1;
    }

    private static byte[] copyOfRange(byte[] original, int from, int to) {
        int newLength = to - from;

        if (newLength < 0) {
            throw new IllegalArgumentException(from + " > " + to);
        }

        byte[] copy = new byte[newLength];

        System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));

        return copy;
    }

    public static byte[] extractBufferLeftOver(byte[] buffer) {

        int lastPositionOfNewlineChar = findLastNewlineChar(buffer);

        int leftOverLength = (buffer.length - 1) - lastPositionOfNewlineChar;
        byte[] leftOver = new byte[leftOverLength];

        int leftOverIndex = 0;
        for (int i = lastPositionOfNewlineChar + 1; i < buffer.length; i++) {
            leftOver[leftOverIndex] = buffer[i];
            leftOverIndex++;
        }

        return leftOver;
    }

    public static byte[] copyBackwardFromSourceOffset(byte[] source, int offsetBackward, byte[] dest) {
        int sourceLengthToCopy = source.length - offsetBackward;

        if (dest.length < sourceLengthToCopy) {
            throw new IllegalArgumentException("The dest does not have enough spaces to hold the source content: "
                    + dest.length + "<" + sourceLengthToCopy);
        }

        int srcPos = sourceLengthToCopy - 1;
        int destPos = dest.length - 1;
        SearchDirection search = SearchDirection.BACKWARD;

        for (; search.isOver(srcPos, 0); srcPos = search.next(srcPos)) {

            dest[destPos] = source[srcPos];
            destPos = search.next(destPos);
        }

        return dest;
    }

    public static byte[] copyBackwardToDestOffset(byte[] source, byte[] dest, int offsetBackward) {
        int remainingSpaceInDest = dest.length - offsetBackward;

        if (remainingSpaceInDest < source.length) {
            throw new IllegalArgumentException("Length of source array is bigger than"
                    + " the remaining spaces of dest array: " + source.length + ">" + remainingSpaceInDest);

        }

        int sourceTracker = source.length - 1;
        int destTracker = remainingSpaceInDest - 1;

        for (; sourceTracker >= 0; sourceTracker--) {
            dest[destTracker] = source[sourceTracker];
            destTracker--;
        }

        return dest;
    }
    
    private static final byte[] EMPTY_CONTENT = new byte[0];
}
