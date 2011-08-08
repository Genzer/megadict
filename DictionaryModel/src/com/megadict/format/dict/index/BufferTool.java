package com.megadict.format.dict.index;

public class BufferTool {

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

    private BufferTool() {
    }

    public static String firstHeadWordIn(byte[] buffer) {
        return (buffer.length == 0) ? "" : findAndExtractFirstHeadWord(buffer);
    }

    private static String findAndExtractFirstHeadWord(byte[] buffer) {
        int maxCharsToFind = 100;
        int firstTabChar = findFirstTabCharInBeginningChars(maxCharsToFind, buffer);
        byte[] firstWordInBytes = wasNotFound(firstTabChar) ? EMPTY_CONTENT : copyOfRange(buffer, 0, firstTabChar);
        return new String(firstWordInBytes);
    }

    private static boolean wasNotFound(int returnedPosition) {
        return returnedPosition == NOT_FOUND;
    }

    private static int findFirstTabCharInBeginningChars(int numOfChars, byte[] byteArray) {
        int start = 0;
        int end = Math.min(numOfChars, byteArray.length);
        char tabChar = '\t';

        return findFowardFirstOccurrenceOfCharInRange(byteArray, start, end, tabChar);
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

    public static byte[] cleanLeftOver(byte[] buffer) {
        int newlineChar = findLastNewlineChar(buffer);
        return wasNotFound(newlineChar) ? EMPTY_CONTENT : copyOfRange(buffer, 0, newlineChar);
    }

    public static byte[] concatenate(byte[] arrayA, byte[] arrayB) {
        byte[] newArray = new byte[arrayA.length + arrayB.length];
        System.arraycopy(arrayA, 0, newArray, 0, arrayA.length);
        System.arraycopy(arrayB, 0, newArray, arrayA.length, arrayB.length);
        return newArray;
    }

    public static byte[] copyBackward(byte[] source, byte[] dest) {
        return copyBackwardWithOffset(source, 0, dest);
    }

    public static byte[] copyBackwardWithOffset(byte[] source, int offsetBackward, byte[] dest) {
        int sourceLengthToCopy = source.length - offsetBackward;

        if (dest.length < sourceLengthToCopy) {
            throw new IllegalArgumentException("Length of source array is bigger than dest array's: "
                    + sourceLengthToCopy + ">" + dest.length);
        }

        int sourceTracker = sourceLengthToCopy - 1;
        int destTracker = dest.length - 1;

        for (; sourceTracker >= 0; sourceTracker--) {
            dest[destTracker] = source[sourceTracker];
            destTracker--;
        }

        return dest;
    }

    public static byte[] copyBackwardWithDestOffset(byte[] source, byte[] dest, int offsetBackward) {
        int remainingSpaceInDest = dest.length - offsetBackward;

        if (remainingSpaceInDest < source.length) {
            throw new IllegalArgumentException(
                    "Length of source array is bigger than the remaining spaces of dest array: " + source.length + ">"
                            + remainingSpaceInDest);

        }
        
        int sourceTracker = source.length - 1;
        int destTracker = remainingSpaceInDest - 1;
        
        for (; sourceTracker >= 0; sourceTracker--) {
            dest[destTracker] = source[sourceTracker];
            destTracker--;
        }
        
        return dest;
    }

    private static final byte NOT_FOUND = -1;
    private static final byte[] EMPTY_CONTENT = new byte[0];
}
