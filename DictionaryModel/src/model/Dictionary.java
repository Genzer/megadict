package model;

import java.util.List;
import java.util.Set;

public interface Dictionary {

    public Set<String> getAllWords();

    public List<String> recommendWord(String word);

    public Definition lookUp(String word);

    public String getName();

}