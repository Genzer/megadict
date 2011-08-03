package com.megadict.format.dict.index;

import java.io.*;
import java.util.*;
import com.megadict.exception.*;

public class ReadLineSegmentBuilder implements SegmentBuilder {

    public ReadLineSegmentBuilder(File indexFile) {
        this.indexFile = indexFile;
        this.parentSegmentFolder = computeSegmentFolderPath();
        createSegmentFolderIfDoesNotExist();
    }

    private String computeSegmentFolderPath() {
        return indexFile.getParent() + File.separator + FOLDER_NAME;
    }

    private void createSegmentFolderIfDoesNotExist() {
        File folder = new File(parentSegmentFolder);
        if (!folder.exists()) {
            boolean folderCreated = folder.mkdir();
            if (folderCreated == false) {
                throw new OperationFailedException("creating index segment folder");
            }
        }
    }

    public List<Segment> builtSegments() {
        return segments;
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
                    currentSegmentNumber++;
                    Segment segment = createSegment(lines);
                    segments.add(segment);
                    saveSegmentToFile(segment, lines);
                    lines = makeLinesBuffer();
                }
            }
        } catch (FileNotFoundException fnf) {
            throw new ResourceMissingException(indexFile);
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
        return new BufferedReader(new FileReader(indexFile), BUFFER_SIZE);
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
        return new File(computeCurrentSegmentPath());
    }

    private String computeCurrentSegmentPath() {
        return String.format(SEGMENT_FULL_PATH_PATTERN, parentSegmentFolder, currentSegmentNumber);
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

    // private static final String LINE_SEPARATOR =
    // System.getProperty("line.separator");
    private static final String LINE_SEPARATOR = "\n";
    private static final String FOLDER_NAME = "splitted";
    private static final String SEGMENT_FULL_PATH_PATTERN = "%s\\s%d.index";
    private static final int BUFFER_SIZE = 8 * 1024;
    private static final int LINE_PER_SEGMENT = 200;

    private final File indexFile;
    private final String parentSegmentFolder;

    private int currentSegmentNumber = 0;
    private List<Segment> segments = new ArrayList<Segment>();
}
