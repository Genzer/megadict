package com.megadict.format.dict.index;

import java.io.*;
import java.util.*;
import com.megadict.exception.*;

class CustomBufferedSegmentBuilder extends BaseSegmentBuilder implements SegmentBuilder {

    public CustomBufferedSegmentBuilder(File indexFile) {
        super(indexFile);
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
            throw new ResourceMissingException(getIndexFile());
        } catch (IOException ioe) {
            throw new OperationFailedException("reading index file", ioe);
        } finally {
            if (reader != null) {
                closeReader(reader);
            }
        }
    }

    private DataInputStream makeReader() throws FileNotFoundException {
        return new DataInputStream(new FileInputStream(getIndexFile()));
    }
    
    private void clearBuffer() {
        
    }

    private Segment createSegment() {
        countCreatedSegment();
        String lowerbound = firstWordInBlock();
        String upperbound = lastWordInBlock();
        File currentSegmentFile = makeCurrentSegmentFile();
        return new Segment(lowerbound, upperbound, currentSegmentFile);
    }

    private File makeCurrentSegmentFile() {
        return new File(computeCurrentSegmentPath());
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

    private byte[] buffer = new byte[BUFFER_SIZE];
    private byte[] secondBuffer = new byte[BUFFER_SIZE];
    private byte[] leftOver = new byte[500];
}

