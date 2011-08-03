package com.megadict.format.dict.index;

import java.util.List;

public interface SegmentBuilder {

    public List<Segment> builtSegments();

    public void build();

}