package com.megadict.format.dict.index.segment;

import java.io.*;
import java.util.*;

public class SegmentIndexWriter {

    public SegmentIndexWriter(File segmentIndexFile, Collection<Segment> segments) {
        this.indexFile = segmentIndexFile;
        this.segments = segments;
        this.writingStrategy = new SegmentIndexWritingStrategy();
    }
    
    public void write(SegmentIndexWritingStrategy strategy) {
        this.writingStrategy = strategy;
        write();
    }

    public void write() {
        DataOutputStream writer = makeWriter(indexFile);
        try {
            doWrite(writer);
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
            BufferedOutputStream bufferedStream = new BufferedOutputStream(rawStream, 16 * 1024);
            return new DataOutputStream(bufferedStream);
        } catch (FileNotFoundException fnf) {
            throw new RuntimeException(fnf);
        }
    }

    private void doWrite(DataOutputStream writer) throws IOException {
        writingStrategy.write(writer, segments);
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

    private Collection<Segment> segments;
    private SegmentIndexWritingStrategy writingStrategy;
    private File indexFile;
}
