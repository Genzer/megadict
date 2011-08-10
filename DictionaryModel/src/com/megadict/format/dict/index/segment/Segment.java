package com.megadict.format.dict.index.segment;

import java.io.File;
import java.io.Serializable;

public class Segment implements Serializable {

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
    
    @Override
    public String toString() {
        return String.format(TO_STRING, lowerbound, upperbound, segmentFile);
    }
    
    @Override
    public boolean equals(Object obj) {
        return ifSameIdentity(obj) ? true : checkCriticalValuesIf(obj, isSameClass(obj));        
    }
    
    private boolean ifSameIdentity(Object obj) {
        return this == obj;
    }
    
    private boolean isSameClass(Object obj) {
        return obj instanceof Segment;
    }
    
    private boolean checkCriticalValuesIf(Object obj, boolean isSameClass) {
        return isSameClass ? compareCriticalValues((Segment) obj) : false;
    }
    
    private boolean compareCriticalValues(Segment other) {
        boolean lowerboundEquals = this.lowerbound.equals(other.lowerbound);
        boolean upperboundEquals = this.upperbound.equals(other.upperbound);
        
        return lowerboundEquals && upperboundEquals;
    }
    
    @Override
    public int hashCode() {
        StringBuilder builder = new StringBuilder();
        builder.append(lowerbound)
            .append(upperbound);
        return builder.toString().hashCode();
    }
    
    
    private String lowerbound;
    private String upperbound;
    private File segmentFile;
    private static final String TO_STRING =  "Segment[lowerbound: %s, upperbound: %s, file: %s]";
    private static final long serialVersionUID = -8851594512525773937L;
}
