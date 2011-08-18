package com.megadict.format.dict.index.segment;

import java.io.*;
import java.util.*;

class SegmentIndexWriter {

    public SegmentIndexWriter(File segmentIndexFile, List<Segment> segments) {
        this.indexFile = segmentIndexFile;
        this.segments = segments;
    }

    public void write() {
        DataOutputStream writer = makeWriter(indexFile);
        try {
            doWrite(segments, writer);
            writer.flush();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        } finally {
            closeWriter(writer);
        }
    }

    private DataOutputStream makeWriter(File file) {
        try {
            FileOutputStream rawStream = new FileOutputStream(file);
            BufferedOutputStream bufferedStream = new BufferedOutputStream(rawStream, 8 * 1024);
            return new DataOutputStream(bufferedStream);
        } catch (FileNotFoundException fnf) {
            throw new RuntimeException(fnf);
        }
    }

    private void doWrite(List<Segment> segments, DataOutputStream writer) throws IOException {
        writeNumOfSegments(writer, segments.size());
        writeAllSegments(writer, segments);
    }

    private void writeNumOfSegments(DataOutputStream writer, int numOfSegments) throws IOException {
        writer.writeInt(numOfSegments);
    }

    private void writeAllSegments(DataOutputStream writer, List<Segment> segments) throws IOException {
        for (Segment segment : segments) {
            writeLowerbound(writer, segment.lowerbound());
            writeUpperbound(writer, segment.upperbound());
            writeFile(writer, segment.file());
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

    private void writeFile(DataOutputStream writer, File file) throws IOException {
        byte[] filePathInByteArray = file.getAbsolutePath().getBytes();
        writer.writeInt(filePathInByteArray.length);
        writer.write(filePathInByteArray);
    }

    private void closeWriter(DataOutputStream writer) {
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private List<Segment> segments;
    private File indexFile;
}
