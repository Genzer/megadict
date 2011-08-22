package com.megadict.format.dict.index.segment;


public class Segment {
    
    private static final String TO_STRING = "Segment[lowerbound: %s, upperbound: %s," +
    " offset: %d, length: %d]";
    
    private final String lowerbound;
    private final String upperbound;
    private final int offset;
    private final int length;
    

    public Segment(String lowerbound, String upperbound) {
        this(lowerbound, upperbound, 0, 0);
    }

    public Segment(String lowerbound, String upperbound, int offset, int length) {
        this.lowerbound = lowerbound;
        this.upperbound = upperbound;
        this.offset = offset;
        this.length = length;
    }

    public String lowerbound() {
        return lowerbound;
    }

    public String upperbound() {
        return upperbound;
    }
    
    public int offset() {
        return offset;
    }

    public int length() {
        return length;
    }

    public boolean contains(String string) {
        if (string == null) {
            return false;
        }

        return isInRange(string);
    }

    private boolean isInRange(String string) {
        boolean isGreaterThanLowerBound = string.compareTo(lowerbound) >= 0;
        boolean isLessThanUpperBound = string.compareTo(upperbound) < 0;

        return isGreaterThanLowerBound && isLessThanUpperBound;
    }

    @Override
    public String toString() {
        return String.format(TO_STRING, lowerbound, upperbound,
                offset, length);
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
        builder.append(lowerbound).append(upperbound);
        return builder.toString().hashCode();
    }
}
