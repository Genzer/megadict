package com.megadict.format.dict.index.segment;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;

public interface SegmentIndexWritingStrategy {

    public void write(DataOutputStream writer, Collection<Segment> segments) throws IOException;

}