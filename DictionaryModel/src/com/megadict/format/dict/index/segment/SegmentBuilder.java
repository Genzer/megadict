package com.megadict.format.dict.index.segment;

import java.io.File;
import java.util.List;

public interface SegmentBuilder {

    boolean checkIfSegmentIndexExists();

    void saveSegmentIndex();
    void saveSegmentIndex(File outputFile);
    
    void loadSavedSegmentIndex();
    void loadSavedSegmentIndex(File inputFile);
    
    void build();
    List<Segment> builtSegments();

}