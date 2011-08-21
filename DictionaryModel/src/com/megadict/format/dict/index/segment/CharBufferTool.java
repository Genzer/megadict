package com.megadict.format.dict.index.segment;

class CharBufferTool {

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

    public static int findFirstNewlineChar(char[] content) {
        int start = 0;
        int end = content.length;
        char newlineChar = '\n';

        return findForwardFirstCharInRange(content, start, end, newlineChar);
    }

    public static int findLastNewlineChar(char[] byteArray) {
        int start = byteArray.length - 1;
        int end = 0;
        char newlineChar = '\n';

        return findBackwardFirstCharInRange(byteArray, start, end, newlineChar);
    }

    public static int findForwardFirstCharInRange(char[] searchArray, int start, int end, char charToFind) {
        return findFirstCharInRange(searchArray, start, end, charToFind, SearchDirection.FORWARD);
    }

    public static int findBackwardFirstCharInRange(char[] searchArray, int start, int end, char charToFind) {
        return findFirstCharInRange(searchArray, start, end, charToFind, SearchDirection.BACKWARD);
    }

    private static int findFirstCharInRange(char[] searchArray, int start, int end, char charToFind,
            SearchDirection search) {

        for (int i = start; search.isOver(i, end); i = search.next(i)) {
            if (searchArray[i] == (byte) charToFind) {
                return i;
            }
        }
        return -1;
    }

    public static char[] copyOfRange(char[] original, int from, int to) {
        int newLength = to - from;

        if (newLength < 0) {
            throw new IllegalArgumentException(from + " > " + to);
        }

        char[] copy = new char[newLength];

        System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));

        return copy;
    }
}
