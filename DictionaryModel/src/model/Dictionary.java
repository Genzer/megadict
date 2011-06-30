package model;

import java.util.List;

public interface Dictionary {

    public List<String> recommendWord(String word);

    public Definition lookUp(String word);

    public String getName();

}