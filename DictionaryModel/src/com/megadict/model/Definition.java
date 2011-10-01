package com.megadict.model;

/**
 * The {@code Definition} class contains the definition of the word in every
 * {@code Dictionary}.
 */

public class Definition {

    private final boolean exists;
    private final String word;
    private final String content;
    private final String dictionaryName;
    
    protected Definition(String word, String definition, String dictionaryName, boolean exists) {
        this.word = word;
        this.content = definition;
        this.dictionaryName = dictionaryName;
        this.exists = exists;
    }
    
    public static Definition makeDefinition(String word, String content, String dictionaryName) {
        return new Definition(word, content, dictionaryName, true);
    }
    
    public static Definition makeNonExists(String word, String placeholderContent, String dictionaryName) {
        return new Definition(word, placeholderContent, dictionaryName, false);
    }

    /**
     * @return - the word of this definition.
     */
    public String getWord() {
        return this.word;
    }

    /**
     * @return - the content of this definition.
     */
    public String getContent() {
        return this.content;
    }
    
    /**
     * @return - true if this definition is found in the dictionary, otherwise, return false.
     */
    public boolean exists() {
        return exists;
    }

    /**
     * @return - the name of the dictionary in which this definition is defined.
     */
    public String getDictionaryName() {
        return this.dictionaryName;
    }
}
