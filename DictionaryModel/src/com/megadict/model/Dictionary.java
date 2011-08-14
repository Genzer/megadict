package com.megadict.model;

import java.util.List;

/**
 * The {@code Dictionary} interface defines a set of methods that every
 * dictionary format must implement.
 * 
 */

public interface Dictionary {

    /**
     * Searches and returns a list of words that are similar to the given word
     * (may be include itself)
     * 
     * @param word
     *            - the word is used to search.
     * @return a list of similar words.
     */
    public List<String> recommendWord(String word);

    /**
     * Searches and returns a list of words that are similar to the given word
     * (may be include itseft).
     * 
     * @param word
     * @param numOfWord - number of recommended words that you preffered.
     */
    public List<String> recommendWord(String word, int prefferedNumOfWord);

    /**
     * Looks up the given word and return a {@code Definiton} object.
     * 
     * @param word
     *            - the word to look up.
     * @return the {@code Definition} of given word if success, otherwise
     *         Definition.NOT_FOUND will be returned.
     */
    public Definition lookUp(String word);

    /**
     * Returns the name of this Dictionary.
     * 
     * @return - name of this dictionary
     */
    public String getName();

}