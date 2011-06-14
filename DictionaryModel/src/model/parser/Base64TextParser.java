package model.parser;


/**
 * This class parses string encoded in base64 to integer value.
 * 
 * @author Genzer
 * 
 */
public class Base64TextParser  {
    
    public static int parse(String text) {
        validateText(text);
        return decodeTextToInt(text);
    }    

    private static void validateText(String text) {
        if (isNull(text)) {
            throw new NullPointerException();
        }
        
        if (!isBase64Encoded(text)) {
            throw new IllegalArgumentException("The text is not encoded with base64");
        }
    }

    private static boolean isNull(String text) {
        return text == null;
    }

    private static int decodeTextToInt(String text) {

        byte[] octets = text.getBytes();        
        int result = 0;        
        int maxExponent = octets.length - 1;
        
        for (int pos = 0; pos < octets.length; pos++) {            
            byte octet = octets[pos];            
            int computedInt = computeIntValueOfCharInString(octet, pos, maxExponent);            
            result += computedInt;
        }
        
        return result;
    }
    
    private static int computeIntValueOfCharInString(byte encodedChar,
            int positionInString, int stringLength) {
        
        byte decodedValue = lookUpValueInDecodeTable(encodedChar);
        int exponent = stringLength - positionInString;
        int factor = (int) Math.pow(NUM_BASE, exponent);        
        return decodedValue*factor;        
    }
    
    private static byte lookUpValueInDecodeTable(byte octet) {
        return DECODE_TABLE[octet - OFFSET_IN_ASCII_TABLE];
    }
    
    public static boolean isBase64Encoded(String text) {
        return isBase64Encoded(text.getBytes());
    }
    
    private static boolean isBase64Encoded(byte[] octets) {
        for (byte octet : octets) {
            if (!isBase64Encoded(octet)) {
                return false;
            }
        }
        return true;
    }
    
    private static boolean isBase64Encoded(byte octet) {
        return isInDecodeTable(octet);
    }
    
    private static boolean isInDecodeTable(byte octet) {
        return (octet >= 38 && octet <= 123) && DECODE_TABLE[octet - 38] != -1;
    }
    
    /**
     * This decode table is a map of ASCII table and base64 character table.
     * 
     * Orgininal idea comes from apache commons codec.
     */
    private static byte[] DECODE_TABLE = {
            -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58,
            59, 60, 61, -1, -1, -1, -1, -1, -1, -1,  0,  1,  2,  3,  4,  5,  6,
             7,  8,  9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23,
            24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34,
            35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51            
    };

    private static byte NUM_BASE = 64;
    private static byte OFFSET_IN_ASCII_TABLE = 38;
}
