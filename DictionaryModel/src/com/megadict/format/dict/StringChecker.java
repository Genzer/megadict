package com.megadict.format.dict;

public class StringChecker {
    
    private StringChecker() {}
    
    public static boolean check(String string) {
        boolean validated = true;
        
        if (nullityCheck(string)) {
            validated = false;
        } else if (emptyStringCheck(string)) {
            validated = false;
        }
        
        return validated;
    }
    
    private static boolean nullityCheck(String string) {
        return string == null;
    }
    
    private static boolean emptyStringCheck(String string) {
        return (string.trim().length() == 0);
    }  
    
}
