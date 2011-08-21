package com.megadict.format.dict.index.segment;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;

class HumanReadableWritingStrategy implements SegmentIndexWritingStrategy {

    @Override
    public void write(DataOutputStream writer, Collection<Segment> segments) throws IOException {
        writeNumOfSegments(writer, segments.size());
        writeAllSegments(writer, segments);
    }
    
    private void writeNumOfSegments(DataOutputStream writer, int numOfSegments) throws IOException {
        writer.writeBytes(Integer.toString(numOfSegments));
        writer.write('\n');
    }
    
    private void writeAllSegments(DataOutputStream writer, Collection<Segment> segments) throws IOException {
        for (Segment segment : segments) {
            writer.writeBytes(segment.toString());
            writer.write('\n');
        }
    }
}
