package com.megadict.format.dict.util;

import java.io.*;
import java.nio.charset.Charset;

import com.megadict.exception.OperationFailedException;
import com.megadict.exception.ResourceMissingException;

public class FileUtil {

    public static final int DEFAULT_BUFFER_SIZE_IN_BYTES = 8 * 1024;
    public static final int LARGE_BUFFER_SIZE_IN_BYTES = 18 * 1024;

    private static final Charset UNICODE = Charset.forName("UTF-8");

    public static final Reader newFileReader(File file) {
        try {
            return new FileReader(file);
        } catch (FileNotFoundException fnf) {
            throw new ResourceMissingException(file);
        }
    }

    public static final Reader newUnicodeBufferedReader(File file) {
        return newUnicodeBufferedReader(file, DEFAULT_BUFFER_SIZE_IN_BYTES);
    }
    
    public static final Reader newUnicodeBufferedReader(File file, int preferredBufferSize) {
        InputStream rawStream = newRawInputStream(file);
        Reader unicodeReader = new InputStreamReader(rawStream, UNICODE);
        return new BufferedReader(unicodeReader, preferredBufferSize);
    }
    
    public static final Writer newFileWriter(File file) {
        try {
            return new FileWriter(file);
        } catch (FileNotFoundException fnf) {
            throw new ResourceMissingException(file, fnf);
        } catch (IOException ioe) {
            throw new OperationFailedException("creating FileWriter", ioe);
        }
    }
    
    public static final Writer newUnicodeBufferedWriter(File file) {
        return new BufferedWriter(newFileWriter(file), DEFAULT_BUFFER_SIZE_IN_BYTES);
    }

    public static final InputStream newRawInputStream(File file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException fnf) {
            throw new ResourceMissingException(file);
        }
    }

    public static final InputStream newBufferedInputStream(File file) {
        return new BufferedInputStream(newRawInputStream(file), DEFAULT_BUFFER_SIZE_IN_BYTES);
    }

}
