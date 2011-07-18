package com.megadict.model;

import java.util.*;

import com.megadict.model.Dictionary;

/**
 * This {@code DictionaryModel} class is the main {@code Model} for dictionary
 * application.
 * 
 */

public class DictionaryModel {

    public DictionaryModel() {
        // TODO: Add initialize code here
        // We need to add default dictionaries here
    }

    /**
     * Performs looking up the given word on every installed dictionaries, then
     * returns a list of all possible {@code Definition}s.
     * 
     * @param word
     *            - the word to look up.
     * @return - a list of {@code Definition}s.
     * @see {@link Dictionary#lookUp(String)}
     */
    public List<Definition> lookUp(String word) {
        // TODO: Traverse all installed dictionary then look
        // the word up on each dictionary
        return Collections.emptyList();
    }

    /**
     * Performs looking up the given word on specified dictionary.
     * 
     * <p>
     * Every installed dictionary should have a unique name, otherwise, the
     * first dictionary matches with the specified dictionary name will be
     * chosen.
     * </p>
     * 
     * @param word
     *            - the word to look up.
     * @param particularDict
     *            - the preferred dictionary.
     * @return - the definiton of the word.
     * @see {@link Dictionary#lookUp(String)}
     */
    public Definition lookUp(String word, String particularDict) {
        // TODO: Return dummy object util we has concrete implementation.
        return Definition.NOT_FOUND;
    }

    /**
     * Returns a list of all installed dictionaries.
     * @return - the list of installed dictionaries.
     */
    public List<String> getInstalledDictionaries() {
        List<String> dictNames = new ArrayList<String>(dictionaries.size());

        for (Dictionary dict : dictionaries) {
            dictNames.add(dict.getName());
        }

        return dictNames;
    }

    // public void addDictionary(Dictionary dict);
    // public void installDictionary(String folder);

    private List<Dictionary> dictionaries = Collections.emptyList();
}
