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
        byte[] unicodeContent = Integer.toString(numOfSegments).getBytes("UTF-8");
        writer.write(unicodeContent);
        writer.write('\n');
    }
    
    private void writeAllSegments(DataOutputStream writer, Collection<Segment> segments) throws IOException {
        for (Segment segment : segments) {
            writer.write(segment.toString().getBytes("UTF-8"));
            writer.write('\n');
        }
    }
}
