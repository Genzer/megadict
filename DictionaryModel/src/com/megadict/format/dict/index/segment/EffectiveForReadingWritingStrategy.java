package com.megadict.format.dict.index.segment;

import java.io.*;
import java.util.*;

class EffectiveForReadingWritingStrategy implements SegmentIndexWritingStrategy {

    @Override
    public void write(DataOutputStream writer, Collection<Segment> segments) throws IOException {
        writeNumOfSegments(writer, segments.size());
        writeAllSegments(writer, segments);
    }

    private void writeNumOfSegments(DataOutputStream writer, int numOfSegments) throws IOException {
        writer.writeInt(numOfSegments);
    }

    private void writeAllSegments(DataOutputStream writer, Collection<Segment> segments) throws IOException {
        for (Segment segment : segments) {
            writeLowerbound(writer, segment.lowerbound());
            writeUpperbound(writer, segment.upperbound());
            writeOffset(writer, segment.offset());
            writeLength(writer, segment.length());
        }
    }

    private void writeLowerbound(DataOutputStream writer, String lowerbound) throws IOException {
        byte[] lowerboundInByteArray = lowerbound.getBytes();
        writer.writeInt(lowerboundInByteArray.length);
        writer.write(lowerboundInByteArray);
    }

    private void writeUpperbound(DataOutputStream writer, String upperbound) throws IOException {
        byte[] upperboundInByteArray = upperbound.getBytes();
        writer.writeInt(upperboundInByteArray.length);
        writer.write(upperboundInByteArray);
    }
    
    private void writeOffset(DataOutputStream writer, int offset) throws IOException {
        writer.writeInt(offset);
    }
    
    private void writeLength(DataOutputStream writer, int length) throws IOException {
        writer.writeInt(length);
    }

    @SuppressWarnings("unused")
    //TODO: consider delete this method
    private void writeFile(DataOutputStream writer, File file) throws IOException {
        byte[] filePathInByteArray = file.getAbsolutePath().getBytes();
        writer.writeInt(filePathInByteArray.length);
        writer.write(filePathInByteArray);
    }
}
