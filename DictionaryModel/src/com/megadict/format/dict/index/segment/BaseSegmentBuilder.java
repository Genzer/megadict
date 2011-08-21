package com.megadict.format.dict.index.segment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

abstract class BaseSegmentBuilder implements SegmentBuilder {

    public BaseSegmentBuilder(File indexFile) {
        this.indexFile = indexFile;
    }
    
    protected File indexFile() {
        return indexFile;
    }
    
    @Override
    public List<Segment> builtSegments() {
        return createdSegments;
    }

    protected void storeCreatedSegment(Segment segment) {
        createdSegments.add(segment);
    }
    
    private final File indexFile;
    private List<Segment> createdSegments = new ArrayList<Segment>();
}
