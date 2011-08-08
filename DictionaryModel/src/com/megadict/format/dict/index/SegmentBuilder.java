package com.megadict.format.dict.index;

import java.util.List;

interface SegmentBuilder {
    
    boolean checkIfSegmentIndexExists();
    
    void saveSegmentIndex();
    
    void loadSavedSegmentIndex();

    List<Segment> builtSegments();

    void build();

}