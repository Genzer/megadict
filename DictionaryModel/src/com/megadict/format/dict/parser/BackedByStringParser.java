package com.megadict.format.dict.parser;

import com.megadict.exception.ParseIndexException;
import com.megadict.format.dict.index.Index;
import com.megadict.model.parser.Base64TextParser;

class BackedByStringParser implements IndexParser {

    @Override
    public Index parse(String text) {
        extractValues(text);
        if (!inputIsValid()) {
            throwException(text);
        }
        return newIndex();
    }

    private void extractValues(String text) {
        splittedValues = text.split(TAB_DELEMITER);
    }

    private boolean inputIsValid() {
        return isEnoughElements() && bothIntValuesIsBase64Encoded();
    }
    
    private boolean isEnoughElements() {
        return splittedValues.length == 3;
    }
    
    private boolean bothIntValuesIsBase64Encoded() {
        return Base64TextParser.isBase64Encoded(splittedValues[OFFSET])
        && Base64TextParser.isBase64Encoded(splittedValues[LENGTH]);
    }

    private Index newIndex() {
        String word = new String(splittedValues[WORD]);
        int offset = toInt(splittedValues[OFFSET]);
        int length = toInt(splittedValues[LENGTH]);
        return new Index(word, offset, length);
    }

    private int toInt(String intValueInString) {
        return Base64TextParser.parseString(intValueInString);
    }
    
    private void throwException(String invalidString) {
        String formattedMessage = formatMessage(invalidString);
        throw new ParseIndexException(formattedMessage, invalidString); 
    }
    
    private static String formatMessage(String invalidString) {
        return String.format("The input string is in invalid format: \"%s\".", invalidString);
    }

    private final int WORD = 0;
    private final int OFFSET = 1;
    private final int LENGTH = 2;

    private static final String TAB_DELEMITER = "\\t";

    private String[] splittedValues;
}
