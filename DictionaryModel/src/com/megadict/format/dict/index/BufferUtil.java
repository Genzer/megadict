package com.megadict.format.dict.index;

import java.util.Arrays;

public class BufferUtil {

    private BufferUtil() {}    
    
    public static String firstWordInBlock(byte[] buffer) {
        
        if (buffer.length == 0) {
            return "";
        }
        
        int contentLength = findFirstTabCharInFirstNumberOfChars(100, buffer);

        byte[] firstWordInBytes = new byte[contentLength];

        for (int i = 0; i < firstWordInBytes.length; i++) {
            firstWordInBytes[i] = buffer[i];
        }

        return new String(firstWordInBytes);
    }
    
    private static int findFirstTabCharInFirstNumberOfChars(int numOfChars, byte[] buffer) {
        
        int searchRange = Math.min(numOfChars, buffer.length);
        
        for (int i = 0; i < searchRange; i++) {
            if (buffer[i] == (byte) '\t') {
                return i;
            }
        }
        return 0;
    }
    
    public static String lastWordInBlock(byte[] buffer) {
        
        if (buffer.length == 0) {
            return "";
        }
     
        int[] pos = findPositionsOfLastTwoNewlineChars(buffer);
        int contentLength = pos[0] - pos[1];
        byte[] fullContent = new byte[contentLength];
        Arrays.fill(fullContent, (byte)' ');
        
        int contentPosition = 0;
        for (int i = pos[1]; i < pos[0]; i++) {
            if (buffer[i] == (byte) '\t') {
                break;
            }
            fullContent[contentPosition++] = buffer[i];
        }
        
        return new String(fullContent).trim();
    }
    
    private static int[] findPositionsOfLastTwoNewlineChars(byte[] buffer) {
        int[] positionOfLastTwoNewlineChars = new int[2];
        int numOfNewlineFound = 0;
        
        for (int i = buffer.length - 1; i >= 0; i--) {
            if (buffer[i] == (byte)'\n') {
                positionOfLastTwoNewlineChars[numOfNewlineFound] = i;
                numOfNewlineFound++;
                if (numOfNewlineFound == 2) {
                    break;
                }
            }
        }
        
        return positionOfLastTwoNewlineChars;
    }
    
}
