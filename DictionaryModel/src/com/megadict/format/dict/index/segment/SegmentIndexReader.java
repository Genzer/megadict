package com.megadict.format.dict.index.segment;

import java.io.*;
import java.util.*;

public class SegmentIndexReader {

    public SegmentIndexReader(File segmentMapFile) {
        this.segmentIndexFile = segmentMapFile;
    }

    public List<Segment> read() {
        DataInputStream reader = makeReader(segmentIndexFile);

        try {
            return doRead(reader);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        } finally {
            closeReader(reader);
        }
    }

    private DataInputStream makeReader(File segmentIndexFile) {
        try {
            FileInputStream rawStream = new FileInputStream(segmentIndexFile);
            BufferedInputStream bufferedStream = new BufferedInputStream(rawStream, 8 * 1024);
            return new DataInputStream(bufferedStream);
        } catch (FileNotFoundException fnf) {
            throw new RuntimeException(fnf);
        }
    }

    private List<Segment> doRead(DataInputStream reader) throws IOException {
        int numOfSegments = reader.readInt();
        return readSegmentsAndAddToList(reader, numOfSegments);
    }
    
    private List<Segment> readSegmentsAndAddToList(DataInputStream reader, int numOfSegments) throws IOException {
        List<Segment> segments = new ArrayList<Segment>(numOfSegments);

        for (int i = 0; i < numOfSegments; i++) {
            String lowerbound = readLowerbound(reader);
            String upperbound = readUpperbound(reader);
            File file = readFile(reader);     

            Segment segment = new Segment(lowerbound, upperbound, file);
            segments.add(segment);
        }

        return segments;
    }
    
    private String readLowerbound(DataInputStream reader) throws IOException {
        int lowerboundSize = reader.readInt();
        byte[] lowerboundInByteArray = new byte[lowerboundSize];
        reader.read(lowerboundInByteArray);
        return new String(lowerboundInByteArray);
    }
    
    private String readUpperbound(DataInputStream reader) throws IOException {
        int upperboundSize = reader.readInt();
        byte[] upperboundInByteArray = new byte[upperboundSize];
        reader.read(upperboundInByteArray);
        return new String(upperboundInByteArray);
    }
    
    private File readFile(DataInputStream reader) throws IOException {
        int filePathSize = reader.readInt();
        byte[] filePathInByteArray = new byte[filePathSize];
        reader.read(filePathInByteArray);
        String filePath = new String(filePathInByteArray);
        return new File(filePath);
    }
    
    private void closeReader(DataInputStream reader) {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private File segmentIndexFile;
}
