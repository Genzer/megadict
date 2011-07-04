package com.megadict.model;

public class Definition {
    
    public static final Definition NOT_FOUND;
    
    static {
        NOT_FOUND = makeNotFound();
    }
        
    private static Definition makeNotFound() {
        return new Definition("Not Found", "There is no definition", "");
    }

    public Definition(String word, String definition, String dictionaryName){
        this.word = word;
        this.content = definition;
        this.dictionaryName = dictionaryName;
    }
    
    public String getWord() {
        return this.word;   
    }
    
    public String getContent() {
        return this.content;
    }
    
    public String getDictionaryName() {
        return this.dictionaryName;
    }
    
    private String word;
    private String content;
    private String dictionaryName;
}
