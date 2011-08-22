package com.megadict.format.dict.index;

import java.io.Serializable;

public class Index implements Serializable {
    
    private static final long serialVersionUID = 688175721394207150L;
    
    private final String word;
    private final int byteOffset;
    private final int byteLength;

    public Index(String word, int offset, int length) {
        this.word = word;
        this.byteOffset = offset;
        this.byteLength = length;
    }

    public String getWord() {
        return this.word;
    }

    public int getByteOffset() {
        return this.byteOffset;
    }

    public int getByteLength() {
        return this.byteLength;
    }

    @Override
    public String toString() {
        return String.format("Index[word: %s, offset: %d, length: %d]", word, byteOffset, byteLength);
    }

    @Override
    public boolean equals(Object obj) {
        return isSameIdentity(obj) ? true : checkCriticalValueIf(obj, isSameClass(obj));
    }

    private boolean isSameIdentity(Object obj) {
        return this == obj;
    }

    private boolean isSameClass(Object obj) {
        return (obj instanceof Index);
    }

    private boolean checkCriticalValueIf(Object obj, boolean isSameClass) {
        return isSameClass ? compareCriticalValue((Index) obj) : false;
    }

    private boolean compareCriticalValue(Index other) {
        boolean contentEqual = this.word.equals(other.word);
        boolean offsetEqual = this.byteOffset == other.byteOffset;
        boolean lengthEqual = this.byteLength == other.byteLength;

        return contentEqual && offsetEqual && lengthEqual;
    }

    @Override
    public int hashCode() {
        return (word + byteOffset + byteLength).hashCode();
    }
}
