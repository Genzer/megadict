package com.megadict.model;

import java.util.*;

import com.megadict.model.Dictionary;


public class DictionaryModel {

    public DictionaryModel() {
        //TODO: Add initialize code here
        // We need to add default dictionaries here
    }
    
    public List<Definition> lookUp(String word) {
        //TODO: Traverse all installed dictionary then look
        // the word up on each dictionary
        return Collections.emptyList();
    }
    
    public Definition lookUp(String word, String particularDict) {
        //TODO: Return dummy object util we has concrete implementation.
        return new Definition("null", "null", "null");
    }
    
    public List<String> getInstalledDictionaries() {
        List<String> dictNames = new ArrayList<String>(dictionaries.size());
        
        for (Dictionary dict : dictionaries) {
            dictNames.add(dict.getName());
        }
        
        return dictNames;
    }
    
    //public void addDictionary(Dictionary dict);
    //public void installDictionary(String folder);
    
    private List<Dictionary> dictionaries = Collections.emptyList();
}
