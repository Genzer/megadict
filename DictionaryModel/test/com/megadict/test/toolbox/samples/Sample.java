package com.megadict.test.toolbox.samples;

class Sample {
    
    public Sample(String headword, String indexString, String expectedContent) {
        this.headword = headword;
        this.indexString = indexString;
        this.expectedContent = expectedContent;
    }
    
    public String getHeadWord() {
        return this.headword;
    }
    
    public String getIndexString() {
        return this.indexString;
    }
    
    public String getExcpectedContent() {
        return this.expectedContent;
    }
    
    private final String headword;
    private final String indexString;
    private final String expectedContent;
}
