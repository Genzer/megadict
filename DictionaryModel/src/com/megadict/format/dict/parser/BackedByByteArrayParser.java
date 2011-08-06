package com.megadict.format.dict.parser;

import com.megadict.exception.ParseIndexException;
import com.megadict.format.dict.index.Index;
import com.megadict.model.parser.Base64TextParser;

class BackedByByteArrayParser implements IndexParser {

    @Override
    public Index parse(String text) {
        if (text == null) {
            throw new NullPointerException("The text to parse must not be null");
        }

        this.text = text.getBytes();

        if (!textHasExactTwoTabChars()) {
            throwException();
        }

        return makeNewIndex();
    }

    private boolean textHasExactTwoTabChars() {
        int numOfTabFound = 0;
        for (int pos = 0; pos < text.length; pos++) {
            numOfTabFound = checkAndRecordFirstTwoTabCharsPosition(pos, numOfTabFound);
            if (numOfTabFound > 2) {
                return false;
            }
        }
        return numOfTabFound == 2;
    }

    private int checkAndRecordFirstTwoTabCharsPosition(int currentPosition, int numOfTabFound) {
        if (isTabChar(text[currentPosition])) {
            numOfTabFound++;
            if (numOfTabFound <= 2) {
                tabPositions[numOfTabFound - 1] = currentPosition;
            }
        }
        return numOfTabFound;
    }

    private static boolean isTabChar(byte c) {
        return c == (byte) '\t';
    }

    private Index makeNewIndex() {
        byte[] headWord = extractHeadWord();
        int offset = toInt(extractOffset());
        int length = toInt(extractLength());
        return new Index(new String(headWord), offset, length);
    }

    private int toInt(byte[] bytes) {
        if (!Base64TextParser.isBase64Encoded(bytes)) {
            throwException();
        }
        return Base64TextParser.parseByteArray(bytes);
    }

    public byte[] extractHeadWord() {
        int headWordSectionBegin = 0;
        int headWordSectionEnd = getFirstTabPosition();
        return extractContent(headWordSectionBegin, headWordSectionEnd);
    }

    public byte[] extractOffset() {
        int offsetSectionBegin = getFirstTabPosition() + 1;
        int offsetSectionEnd = getSecondTabPosition();
        return extractContent(offsetSectionBegin, offsetSectionEnd);
    }

    public byte[] extractLength() {
        int byteLengthSectionBegin = getSecondTabPosition() + 1;
        int byteLengthSectionEnd = text.length;
        return extractContent(byteLengthSectionBegin, byteLengthSectionEnd);
    }

    private int getFirstTabPosition() {
        return tabPositions[0];
    }

    private int getSecondTabPosition() {
        return tabPositions[1];
    }

    private byte[] extractContent(int contentBegin, int contentEnd) {
        int contentCount = 0;
        byte[] result = new byte[contentEnd - contentBegin];
        for (int i = contentBegin; i < contentEnd; i++) {
            result[contentCount] = text[i];
            contentCount++;
        }
        return result;
    }

    private void throwException() {
        String formattedMessage = formatMessage(new String(text));
        throw new ParseIndexException(formattedMessage);
    }
    
    private static String formatMessage(String invalidString) {
        return String.format("The input string is in invalid format: \"%s\".", invalidString);
    }

    private byte[] text;
    private int[] tabPositions = new int[2];
}
