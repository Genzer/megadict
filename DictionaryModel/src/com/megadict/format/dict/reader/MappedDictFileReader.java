package com.megadict.format.dict.reader;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import com.megadict.exception.*;

class MappedDictFileReader implements DictFileReader {

    public MappedDictFileReader(File dictFile) {
        this.dictFile = dictFile;
    }
    
    @Override
    public void open() {
        makeMappedFile();
    }
    
    private void makeMappedFile() {
        if (isMapped == false) {
            reader = makeMappedBuffer(makeReadOnlyRandomAccessFile());
            isMapped = true;
        }
    }
    
    private RandomAccessFile makeReadOnlyRandomAccessFile() {
        try {
            return new RandomAccessFile(dictFile, "r");
        } catch (FileNotFoundException fnf) {
            throw new ResourceMissingException(dictFile, fnf);
        }
    }
    
    private MappedByteBuffer makeMappedBuffer(RandomAccessFile file) {
        FileChannel fc = file.getChannel();
        try {
            return fc.map(MapMode.READ_ONLY, 0, fc.size());
        } catch (IOException ioe) {
            throw new OperationFailedException("creating mapped buffer", ioe);
        }
    }

    @Override
    public byte[] read(int offset, int length) {
        jumpTo(offset);
        byte[] content = read(length);
        return content;
    }
    
    private void jumpTo(int offset) {
        reader.position(offset);
    }
    
    private byte[] read(int length) {
        byte[] content = new byte[length];
        reader.get(content, 0, length);
        return content;
    }

    @Override
    public void close() {
        // Currently do nothing
    }
    
    
    private final File dictFile;
    private MappedByteBuffer reader;
    private boolean isMapped;
}
