package com.megadict.format.dict.index.segment;

import java.io.*;
import java.util.*;

import com.megadict.exception.ResourceMissingException;
import com.megadict.format.dict.util.FileUtil;

public class SegmentIndexWriter {

    private Collection<Segment> segments;
    private SegmentIndexWritingStrategy writingStrategy;
    private File indexFile;

    public SegmentIndexWriter(File segmentIndexFile, Collection<Segment> segments) {
        this.indexFile = segmentIndexFile;
        this.segments = segments;
        this.writingStrategy = new EffectiveForReadingWritingStrategy();
    }

    public void write(SegmentIndexWritingStrategy strategy) {
        this.writingStrategy = strategy;
        write();
    }

    public void write() {
        DataOutputStream writer = null;

        try {
            writer = makeWriter(indexFile);
            doWrite(writer);
            writer.flush();
        } catch (FileNotFoundException fnf) {
            throw new ResourceMissingException(indexFile);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        } finally {
            closeWriter(writer);
        }
    }

    private DataOutputStream makeWriter(File file) throws IOException {
        FileOutputStream rawStream = new FileOutputStream(file);
        BufferedOutputStream bufferedStream = new BufferedOutputStream(rawStream, 16 * 1024);
        return new DataOutputStream(bufferedStream);
    }

    private void doWrite(DataOutputStream writer) throws IOException {
        writingStrategy.write(writer, segments);
    }

    private void closeWriter(DataOutputStream writer) {
        FileUtil.closeOutputStream(writer);
    }
}
