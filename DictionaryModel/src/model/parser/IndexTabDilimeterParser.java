package model.parser;

public class IndexTabDilimeterParser implements IndexParser {
        
    @Override
    public Index parse(String text) {        
        extractValues(text);
        return newIndex();
    }
    
    private void extractValues(String text) {
        splittedValues = text.split(TAB_DELEMITER);
    }
    
    private Index newIndex() {
        String word = splittedValues[WORD];
        
        int offset = toInt(splittedValues[OFFSET]);
        int length = toInt(splittedValues[LENGTH]);
        
        return new Index(word, offset, length);
    }
    
    private int toInt(String intValueInString) {
        return PARSER.parse(intValueInString);
    }
    
    private final int WORD = 0;
    private final int OFFSET = 1;
    private final int LENGTH = 2;
    
    private static final Base64TextParser PARSER = new Base64TextParser();
    private static final String TAB_DELEMITER = "\\t";
    
    private String[] splittedValues;
}
