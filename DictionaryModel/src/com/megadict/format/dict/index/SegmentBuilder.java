package com.megadict.format.dict.index;

import java.util.List;

interface SegmentBuilder {
    
    boolean findSegmentMainIndexIfExists();
    
    void saveSegmentMainIndex();
    
    void loadSavedSegmentMainIndex();

    List<Segment> builtSegments();

    void build();

}