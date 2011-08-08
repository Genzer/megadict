package com.megadict.format.dict.index;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.megadict.exception.OperationFailedException;
import com.megadict.exception.ResourceMissingException;

public abstract class BaseSegmentBuilder implements SegmentBuilder {

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
        return String.format(SEGMENT_FULL_PATH_PATTERN, parentSegmentFolder, numOfCreatedSegment);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void loadSavedSegmentIndex() {
        ObjectInputStream reader = makeObjectReader(segmentMainIndexFile);
        try {
            createdSegments = (List<Segment>) reader.readObject();
        } catch (IOException ioe) {
            throw new OperationFailedException("reading segment main index", ioe);
        } catch (ClassNotFoundException cnf) {
            throw new OperationFailedException("casting read object to original type", cnf);
        } finally {
            closeReader(reader);
        }
    }

    private ObjectInputStream makeObjectReader(File file) {
        try {
            FileInputStream rawStream = new FileInputStream(file);
            DataInputStream dataStream = new DataInputStream(rawStream);
            return new ObjectInputStream(dataStream);
        } catch (FileNotFoundException fnf) {
            throw new ResourceMissingException(segmentMainIndexFile, fnf);
        } catch (IOException ioe) {
            throw new OperationFailedException("creating ObjectInputStream", ioe);
        }
    }
    
    private void closeReader(ObjectInputStream reader) {
        try {
            reader.close();
        } catch (IOException ioe) {
            throw new OperationFailedException("closing ObjectInputStream", ioe);
        }
    }

    protected File indexFile() {
        return indexFile;
    }

    @Override
    public List<Segment> builtSegments() {
        return createdSegments;
    }
    
    protected void storeCreatedSegment(Segment segment) {
        createdSegments.add(segment);
    }

    protected void countCreatedSegment() {
        numOfCreatedSegment++;
    }

    @Override
    public boolean checkIfSegmentIndexExists() {
        return segmentMainIndexFile.exists();
    }

    @Override
    public void saveSegmentIndex() {
        ObjectOutputStream writer = makeObjectWriter(segmentMainIndexFile);

        try {
            writer.writeObject(createdSegments);
            writer.flush();
        } catch (IOException ioe) {
            throw new OperationFailedException("writing segment main index", ioe);
        } finally {
            closeWriter(writer);
        }
    }

    private ObjectOutputStream makeObjectWriter(File file) {
        try {
            FileOutputStream rawStream = new FileOutputStream(file);
            DataOutputStream dataStream = new DataOutputStream(rawStream);
            return new ObjectOutputStream(dataStream);
        } catch (FileNotFoundException fnf) {
            throw new ResourceMissingException(segmentMainIndexFile, fnf);
        } catch (IOException ioe) {
            throw new OperationFailedException("creating ObjectOutputStream", ioe);
        }
    }

    private void closeWriter(ObjectOutputStream writer) {
        try {
            writer.close();
        } catch (IOException ioe) {
            throw new OperationFailedException("closing segment index writer", ioe);
        }
    }

    protected static final int BUFFER_SIZE = 8 * 1024;
    private static final String FOLDER_NAME = "splitted";
    private static final String SEGMENT_FULL_PATH_PATTERN = "%s\\s%d.index";

    private final File indexFile;
    private final File segmentMainIndexFile;
    private final String parentSegmentFolder;
    
    private int numOfCreatedSegment = 0;
    protected List<Segment> createdSegments = new ArrayList<Segment>();
}
