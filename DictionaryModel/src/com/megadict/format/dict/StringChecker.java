package com.megadict.format.dict;

public class StringChecker {
    
    private StringChecker() {}
    
    public static boolean check(String string) {
        boolean passed = true;
        
        passed = nullityCheck(string) && emptyStringCheck(string);
        
        return passed;
    }
    
    private static boolean nullityCheck(String string) {
        return string != null;
    }
    
    private static boolean emptyStringCheck(String string) {
        return (string.trim().length() > 0);
    }  
    
}
