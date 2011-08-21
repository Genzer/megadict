package com.megadict.format.dict.index.segment;

import java.util.List;

public interface SegmentBuilder {    
    void build();
    List<Segment> builtSegments();
}