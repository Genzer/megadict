package com.megadict.format.dict.index;

import java.io.File;

public class Segment {

    public Segment(String lowerbound, String upperbound, File segmentFile) {
        this.lowerbound = lowerbound;
        this.upperbound = upperbound;
        this.segmentFile = segmentFile;
    }
    
    public Segment(String lowerbound, String upperbound) {
        this.upperbound = upperbound;
        this.lowerbound = lowerbound;
    }
    
    
    public String lowerbound() {
        return lowerbound;
    }
    
    
    public String upperbound() {
        return upperbound;
    }
    
    
    public File file() {
        return segmentFile;
    }

    
    public boolean contains(String string) {
        if (string == null) {
            return false;
        }
        
        return isInRange(string);
    }
    
    
    private boolean isInRange(String string) {
        boolean isGreaterThanLowerBound = string.compareTo(lowerbound) >= 0;
        boolean isLessThanUpperBound = string.compareTo(upperbound) <= 0;

        return isGreaterThanLowerBound && isLessThanUpperBound;
    }
    
    private String lowerbound;
    private String upperbound;
    private File segmentFile;
}
