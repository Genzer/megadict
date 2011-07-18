package com.megadict.format.dict.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import com.megadict.exception.OperationFailedException;
import com.megadict.exception.ResourceMissingException;

class GZipDictFileReader implements DictFileReader {

    public GZipDictFileReader(File gzipFile) {
        this.gzipFile = gzipFile;
    }
    
    @Override
    public void open() {
        try {
            FileInputStream fis = new FileInputStream(gzipFile);
            gzipReader = new GZIPInputStream(fis);
        } catch (FileNotFoundException fnf) {
            throw new ResourceMissingException(gzipFile);
        } catch (IOException ioe) {
            throw new OperationFailedException("opening GZIP dict file", ioe);
        }
    }

    @Override
    public byte[] read(int offset, int length) {
        return new byte[0];
    }

    @Override
    public void close() {
        try {
            gzipReader.close();
        } catch (IOException ioe) {
            throw new OperationFailedException("closing reader", ioe);
        }
    }
    
    private final File gzipFile;
    private GZIPInputStream gzipReader;
}
