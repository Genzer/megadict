package com.megadict.format.dict.index;

import java.io.*;
import java.util.*;
import com.megadict.exception.*;

class ReadLineSegmentBuilder extends BaseSegmentBuilder implements SegmentBuilder {

    public ReadLineSegmentBuilder(File indexFile) {
        super(indexFile);
    }

    public List<Segment> builtSegments() {
        return createdSegments;
    }

    public void build() {
        BufferedReader reader = null;
        List<String> lines = makeLinesBuffer();
        String line = "";
        try {
            reader = makeReader();
            while ((line = reader.readLine()) != null) {
                lines.add(line);
                if (lines.size() == LINE_PER_SEGMENT) {
                    countCreatedSegment();
                    Segment segment = createSegment(lines);
                    createdSegments.add(segment);
                    saveSegmentToFile(segment, lines);
                    lines = makeLinesBuffer();
                }
            }
            
            if (lines.size() > 0 ) {
                countCreatedSegment();
                Segment segment = createSegment(lines);
                createdSegments.add(segment);
                saveSegmentToFile(segment, lines);
                lines = makeLinesBuffer();
            }
        } catch (FileNotFoundException fnf) {
            throw new ResourceMissingException(indexFile());
        } catch (IOException ioe) {
            throw new OperationFailedException("reading file", ioe);
        } finally {
            if (reader != null) {
                closeReader(reader);
            }
        }
    }

    private List<String> makeLinesBuffer() {
        return new ArrayList<String>(LINE_PER_SEGMENT);
    }

    private BufferedReader makeReader() throws FileNotFoundException {
        return new BufferedReader(new FileReader(indexFile()), BUFFER_SIZE);
    }

    private Segment createSegment(List<String> lines) {
        String lowerbound = determineFirstWord(lines);
        String upperbound = determineLastWord(lines);
        File segmentFile = makeCurrentSegmentFile();
        return new Segment(lowerbound, upperbound, segmentFile);
    }

    private String determineFirstWord(List<String> lines) {
        String firstLine = lines.get(0);
        String[] splitted = firstLine.split("\t");
        return new String(splitted[0].getBytes());
    }

    private String determineLastWord(List<String> lines) {
        String lastLine = lines.get(lines.size() - 1);
        String[] splitted = lastLine.split("\t");
        return new String(splitted[0].getBytes());
    }

    private File makeCurrentSegmentFile() {
        return new File(determineCurrentSegmentPath());
    }

    private void saveSegmentToFile(Segment segment, List<String> lines) {

        BufferedWriter writer = null;
        try {
            writer = makeWriter(segment.file());

            for (String line : lines) {
                writer.write(line);
                writer.write(LINE_SEPARATOR);
            }

            writer.flush();
        } catch (FileNotFoundException fnf) {
            throw new ResourceMissingException(segment.file());
        } catch (IOException ioe) {
            throw new OperationFailedException("writing segment file", ioe);
        } finally {
            if (writer != null) {
                closeWriter(writer);
            }
        }

    }

    private BufferedWriter makeWriter(File file) throws IOException {
        return new BufferedWriter(new FileWriter(file), BUFFER_SIZE);
    }

    private void closeReader(BufferedReader reader) {
        try {
            reader.close();
        } catch (IOException ioe) {
            throw new OperationFailedException("closing reader", ioe);
        }
    }
    private void closeWriter(BufferedWriter writer) {
        try {
            writer.close();
        } catch (IOException ioe) {
            throw new OperationFailedException("closing reader", ioe);
        }
    }

    private static final String LINE_SEPARATOR = "\n";
    private static final int LINE_PER_SEGMENT = 200;
}
