package com.megadict.format.dict.index;

import java.io.*;
import java.util.*;
import com.megadict.exception.*;

public class CustomBufferSegmentBuilder implements SegmentBuilder {

    public CustomBufferSegmentBuilder(File indexFile) {
        this.indexFile = indexFile;
        parentSegmentFolder = computeSegmentFolderPath();
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

    
    @Override
    public List<Segment> builtSegments() {
        return segments;
    }


    @Override
    public void build() {
        
        DataInputStream reader = null;
        try {
            reader = makeReader();

            while (reader.read(buffer) != -1) {
                Segment segment = createSegment();
                segments.add(segment);
                saveSegmentToFile(segment);  
                resetBuffer();
            }
        } catch (FileNotFoundException fnf) {
            throw new ResourceMissingException(indexFile);
        } catch (IOException ioe) {
            throw new OperationFailedException("reading index file", ioe);
        } finally {
            if (reader != null) {
                closeReader(reader);
            }
        }
    }

    private DataInputStream makeReader() throws FileNotFoundException {
        return new DataInputStream(new FileInputStream(indexFile));
    }

    private Segment createSegment() {
        currentSegmentNumber++;
        String lowerbound = firstWordInBlock();
        String upperbound = lastWordInBlock();
        File currentSegmentFile = makeCurrentSegmentFile();
        return new Segment(lowerbound, upperbound, currentSegmentFile);
    }

    private File makeCurrentSegmentFile() {
        return new File(computeCurrentSegmentPath());
    }

    private String computeCurrentSegmentPath() {
        return String.format(SEGMENT_FULL_PATH_PATTERN, parentSegmentFolder, currentSegmentNumber);
    }

    private String firstWordInBlock() {
        return BufferUtil.firstWordInBlock(buffer);
    }

    private String lastWordInBlock() {
        return BufferUtil.lastWordInBlock(buffer);
    }

    private void saveSegmentToFile(Segment segment) {
        DataOutputStream writer = null;
        try {
            writer = new DataOutputStream(new FileOutputStream(segment.file()));
            writer.write(buffer);
            writer.flush();
        } catch (FileNotFoundException fnf) {
            throw new ResourceMissingException(segment.file());
        } catch (IOException ioe) {
            throw new OperationFailedException("writing segment file", ioe);
        } finally {
            closeWriter(writer);
        }
    }
    
    private void closeReader(DataInputStream reader) {
        try {
            reader.close();
        } catch (IOException ioe) {
            throw new OperationFailedException("closing data input stream", ioe);
        }
    }

    private void closeWriter(DataOutputStream writer) {
        try {
            writer.close();
        } catch (IOException ioe) {
            throw new OperationFailedException("closing writer", ioe);
        }
    }
    
    private void resetBuffer() {
        Arrays.fill(buffer, (byte) '\n');
    }

    private static final String FOLDER_NAME = "splitted";
    private static final String SEGMENT_FULL_PATH_PATTERN = "%s\\s%d.index";
    private static final int BUFFER_SIZE = 8 * 1024;

    private final File indexFile;
    private final String parentSegmentFolder;

    private byte[] buffer = new byte[BUFFER_SIZE];
    private int currentSegmentNumber = 0;
    List<Segment> segments = new ArrayList<Segment>();
}
