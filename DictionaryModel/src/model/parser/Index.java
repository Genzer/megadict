package model.parser;

public class Index {
    
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
    
    public int getByteLenght() {
        return this.byteLength;
    }
    
    @Override
    public String toString() {
        return String.format("Index[word: %s, offset: %d, length: %d]", word, byteOffset, byteLength);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if ( !(obj instanceof Index) ) {
            return false;
        }
        
        Index other = (Index) obj;
        
        boolean contentEqual = this.word.equals(other.word);
        boolean offsetEqual = this.byteOffset == other.byteOffset;
        boolean lengthEqual = this.byteLength == other.byteLength;
        
        return contentEqual && offsetEqual && lengthEqual;
    }
    
    @Override
    public int hashCode() {
        return (word + byteOffset + byteLength).hashCode();
    }
    
    private String word;
    private int byteOffset;
    private int byteLength;
}
