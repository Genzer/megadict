package com.megadict.format.dict.index.segment;

class Block {
    
    Block(int headerLeftOverLength, int footerLeftOverLength, String headword, int offset) {
        this.headerLeftOverLength = headerLeftOverLength;
        this.footerLeftOverLength = footerLeftOverLength;
        this.headword = headword;
        this.offset = offset;
    }
    
   final int headerLeftOverLength;
   final int footerLeftOverLength;
   final String headword;
   int offset;
}
