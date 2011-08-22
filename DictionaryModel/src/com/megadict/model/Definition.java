package com.megadict.model;

/**
 * The {@code Definition} class contains the definition of the word in every
 * {@code Dictionary}.
 */

public class Definition {

    /**
     * It is recommended that every {@code Dictionary} implementation must
     * return this instance when lookup doesn't succeed rather than returning
     * {@code null}.
     */
    public static final Definition NOT_FOUND;

    private final String word;
    private final String content;
    private final String dictionaryName;

    static {
        NOT_FOUND = makeNotFound();
    }

    private static Definition makeNotFound() {
        return new Definition("Not Found", "There is no definition", "");
    }

    public Definition(String word, String definition, String dictionaryName) {
        this.word = word;
        this.content = definition;
        this.dictionaryName = dictionaryName;
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
     * @return - the name of the dictionary in which this definition is defined.
     */
    public String getDictionaryName() {
        return this.dictionaryName;
    }
}
