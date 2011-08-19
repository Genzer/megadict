package com.megadict.format.dict.index.segment;

import java.io.*;
import java.util.*;

import com.megadict.exception.OperationFailedException;

abstract class BaseSegmentBuilder implements SegmentBuilder {

    public BaseSegmentBuilder(File indexFile) {
        this.indexFile = indexFile;
        parentSegmentFolder = determineSegmentFolderPath();
        segmentMainIndexFile = createSegmentIndexFile();
        createSegmentFolderIfNotExist();
    }

    private String determineSegmentFolderPath() {
        return indexFile.getParent() + File.separator + FOLDER_NAME;
    }

    private File createSegmentIndexFile() {
        String segmentMapPath = determineCurrentSegmentPath();
        return new File(segmentMapPath);
    }

    private void createSegmentFolderIfNotExist() {
        File folder = new File(parentSegmentFolder);
        if (!folder.exists()) {
            boolean folderCreated = folder.mkdir();
            if (folderCreated == false) {
                throw new OperationFailedException("creating index segment folder");
            }
        }
    }
    
    protected String determineCurrentSegmentPath() {
        int currentSegmentNumber = createdSegments.size() + 1;
        return String.format(SEGMENT_FULL_PATH_PATTERN, parentSegmentFolder, currentSegmentNumber);
    }
    
    @Override
    public boolean checkIfSegmentIndexExists() {
        return segmentMainIndexFile.exists();
    }

    @Override
    public void saveSegmentIndex() {
        saveSegmentIndex(segmentMainIndexFile);
    }
    
    @Override
    public void saveSegmentIndex(File outputFile) {
        SegmentIndexWriter writer = new SegmentIndexWriter(outputFile, createdSegments);
        writer.write();
    }
    
    @Override
    public void loadSavedSegmentIndex() {
        loadSavedSegmentIndex(segmentMainIndexFile);
    }
    
    @Override
    public void loadSavedSegmentIndex(File inputFile) {
        SegmentIndexReader reader = new SegmentIndexReader(inputFile);
        createdSegments = reader.read();
    }
    
    @Override
    public void build() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<Segment> builtSegments() {
        // TODO Auto-generated method stub
        return null;
    }

    protected File indexFile() {
        return indexFile;
    }

    protected List<Segment> getCreatedSegment() {
        return createdSegments;
    }
    
    protected void storeCreatedSegment(Segment segment) {
        createdSegments.add(segment);
    }
    
    protected static final int BUFFER_SIZE_IN_BYTES = 8 * 1024;
    private static final String FOLDER_NAME = "splitted";
    private static final String SEGMENT_FULL_PATH_PATTERN = "%s\\s%d.index";

    private final File indexFile;
    private final File segmentMainIndexFile;
    private final String parentSegmentFolder;
    
    private List<Segment> createdSegments = new ArrayList<Segment>();
}
