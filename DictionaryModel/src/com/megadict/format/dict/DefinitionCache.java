package com.megadict.format.dict;

import java.util.HashMap;
import java.util.Map;

import com.megadict.model.Definition;

class DefinitionCache {

    private final Map<String, Definition> cachedDefs = new HashMap<String, Definition>();
    
    public void cache(String keyword, Definition def) {
        cachedDefs.put(keyword, def);
    }
    
    public void cache(Definition def) {
        cachedDefs.put(def.getWord(), def);
    }
    
    public boolean contains(String keyword) {
        return cachedDefs.containsKey(keyword);
    }
    
    public Definition get(String keyword) {
        return cachedDefs.get(keyword);
    }
}
