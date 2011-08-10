package com.megadict.format.dict.index.segment;

import java.util.List;

public interface SegmentBuilder {
    
    boolean checkIfSegmentIndexExists();
    
    void saveSegmentIndex();
    
    void loadSavedSegmentIndex();

    List<Segment> builtSegments();

    void build();

}