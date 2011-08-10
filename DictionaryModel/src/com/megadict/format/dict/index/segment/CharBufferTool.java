package com.megadict.format.dict.index.segment;

public class CharBufferTool {

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

    private CharBufferTool() {
    }

    public static String firstHeadWordIn(char[] buffer) {
        return (buffer.length == 0) ? "" : findAndExtractFirstHeadWord(buffer);
    }

    private static String findAndExtractFirstHeadWord(char[] buffer) {
        int maxCharsToFind = 100;
        int firstTabChar = findFirstTabCharInBeginningChars(maxCharsToFind, buffer);
        char[] firstWordInBytes = wasNotFound(firstTabChar) ? EMPTY_CONTENT : copyOfRange(buffer, 0, firstTabChar);
        return new String(firstWordInBytes);
    }

    private static boolean wasNotFound(int returnedPosition) {
        return returnedPosition == NOT_FOUND;
    }

    private static int findFirstTabCharInBeginningChars(int numOfChars, char[] byteArray) {
        int start = 0;
        int end = Math.min(numOfChars, byteArray.length);
        char tabChar = '\t';

        return findFowardFirstOccurrenceOfCharInRange(byteArray, start, end, tabChar);
    }

    public static String lastHeadWordIn(char[] buffer) {
        return (buffer.length == 0) ? "" : findAndExtractLastHeadWord(buffer);
    }

    private static String findAndExtractLastHeadWord(char[] buffer) {
        int newLineChar = findLastNewlineChar(buffer);
        char[] fullContent = wasNotFound(newLineChar) ? EMPTY_CONTENT : copyOfRange(buffer, newLineChar + 1,
                buffer.length);

        int firstTabChar = findFirstTabCharInBeginningChars(fullContent.length, fullContent);
        char[] headWord = wasNotFound(firstTabChar) ? EMPTY_CONTENT : copyOfRange(fullContent, 0, firstTabChar);

        return new String(headWord);
    }

    private static int findLastNewlineChar(char[] byteArray) {
        int start = byteArray.length - 1;
        int end = 0;
        char newlineChar = '\n';

        return findBackwardFirstOccurrenceOfCharInRange(byteArray, start, end, newlineChar);
    }

    private static int findFowardFirstOccurrenceOfCharInRange(char[] searchArray, int start, int end, char charToFind) {
        return findFirstOccurrenceOfCharInRange(searchArray, start, end, charToFind, SearchDirection.FORWARD);
    }

    private static int findBackwardFirstOccurrenceOfCharInRange(char[] searchArray, int start, int end, char charToFind) {
        return findFirstOccurrenceOfCharInRange(searchArray, start, end, charToFind, SearchDirection.BACKWARD);
    }

    private static int findFirstOccurrenceOfCharInRange(char[] searchArray, int start, int end, char charToFind,
            SearchDirection search) {

        for (int i = start; search.isOver(i, end); i = search.next(i)) {
            if (searchArray[i] == (byte) charToFind) {
                return i;
            }
        }
        return -1;
    }

    private static char[] copyOfRange(char[] original, int from, int to) {
        int newLength = to - from;

        if (newLength < 0) {
            throw new IllegalArgumentException(from + " > " + to);
        }

        char[] copy = new char[newLength];

        System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));

        return copy;
    }

    public static char[] extractBufferLeftOver(char[] buffer) {

        int lastPositionOfNewlineChar = findLastNewlineChar(buffer);

        int leftOverLength = (buffer.length - 1) - lastPositionOfNewlineChar;
        char[] leftOver = new char[leftOverLength];

        int leftOverIndex = 0;
        for (int i = lastPositionOfNewlineChar + 1; i < buffer.length; i++) {
            leftOver[leftOverIndex] = buffer[i];
            leftOverIndex++;
        }

        return leftOver;
    }

    public static char[] concatenate(char[] arrayA, char[] arrayB) {
        char[] newArray = new char[arrayA.length + arrayB.length];
        System.arraycopy(arrayA, 0, newArray, 0, arrayA.length);
        System.arraycopy(arrayB, 0, newArray, arrayA.length, arrayB.length);
        return newArray;
    }

    public static char[] copyBackward(char[] source, char[] dest) {
        return copyBackwardFromSourceOffset(source, 0, dest);
    }

    public static char[] copyBackwardFromSourceOffset(char[] source, int offsetBackward, char[] dest) {
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

    public static char[] copyBackwardToDestOffset(char[] source, char[] dest, int offsetBackward) {
        int remainingSpaceInDest = dest.length - offsetBackward;

        if (remainingSpaceInDest < source.length) {
            System.out.println(new String(source));
            System.out.println(new String(dest));
            System.out.println(dest.length);
            System.out.println(offsetBackward);
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
    private static final char[] EMPTY_CONTENT = new char[0];
}
