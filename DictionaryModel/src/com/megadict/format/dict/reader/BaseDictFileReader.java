package com.megadict.format.dict.reader;

import java.io.*;
import com.megadict.exception.OperationFailedException;
import com.megadict.exception.ResourceMissingException;

abstract class BaseDictFileReader implements DictFileReader {

    public BaseDictFileReader(File dictFile) {
        this.dictFile = dictFile;
    }
    
    public void open() {
        try {
            openStream();
        } catch (FileNotFoundException fnf) {
            throw new ResourceMissingException(dictFile, fnf);
        }
    }
    
    protected abstract void openStream() throws FileNotFoundException;

    public byte[] read(int offset, int length) {
        try {
            jumpTo(offset);
            return readWithAmountOf(length);
        } catch (IOException ioe) {
            throw new OperationFailedException("reading file", ioe);
        }
    }
    
    protected abstract void jumpTo(int offset) throws IOException;    
    protected abstract byte[] readWithAmountOf(int length) throws IOException;

    public void close() {
        try {
            closeReader();
        } catch (IOException ioe) {
            throw new OperationFailedException("closing reader", ioe);
        }
    }
    
    protected abstract void closeReader() throws IOException;
    
    protected final File dictFile;
}
