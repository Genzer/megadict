package com.megadict.format.dict.util;

import java.io.*;
import java.nio.charset.Charset;

import com.megadict.exception.OperationFailedException;
import com.megadict.exception.ResourceMissingException;

public class FileUtil {

    public static final int DEFAULT_BUFFER_SIZE_IN_BYTES = 8 * 1024;
    public static final int LARGE_BUFFER_SIZE_IN_BYTES = 18 * 1024;

    public static Reader newFileReader(File file) {
        try {
            return new FileReader(file);
        } catch (FileNotFoundException fnf) {
            throw new ResourceMissingException(file);
        }
    }

    public static Reader newUnicodeBufferedReader(File file) {
        return newUnicodeBufferedReader(file, DEFAULT_BUFFER_SIZE_IN_BYTES);
    }
    
    public static Reader newUnicodeBufferedReader(File file, int preferredBufferSize) {
        InputStream rawStream = newRawInputStream(file);
        Reader unicodeReader = new InputStreamReader(rawStream, UNICODE);
        return new BufferedReader(unicodeReader, preferredBufferSize);
    }
    
    public static Writer newFileWriter(File file) {
        try {
            return new FileWriter(file);
        } catch (FileNotFoundException fnf) {
            throw new ResourceMissingException(file, fnf);
        } catch (IOException ioe) {
            throw new OperationFailedException("creating FileWriter", ioe);
        }
    }
    
    public static Writer newUnicodeBufferedWriter(File file) {
        return new BufferedWriter(newFileWriter(file), DEFAULT_BUFFER_SIZE_IN_BYTES);
    }

    public static InputStream newRawInputStream(File file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException fnf) {
            throw new ResourceMissingException(file);
        }
    }

    public static InputStream newBufferedInputStream(File file) {
        return new BufferedInputStream(newRawInputStream(file), DEFAULT_BUFFER_SIZE_IN_BYTES);
    }
    
    public static void closeReader(Reader reader) {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch(IOException ioe) {
            throw new OperationFailedException("closing reader", ioe);
        }
    }
    
    public static void closeWriter(Writer writer) {
        try {
            if (writer != null) {
                writer.close();
            }
        } catch(IOException ioe) {
            throw new OperationFailedException("closing writer", ioe);
        }
    }
    
    public static void closeInputStream(InputStream reader) {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch(IOException ioe) {
            throw new OperationFailedException("closing input stream", ioe);
        } 
    }
    
    public static void closeOutputStream(OutputStream writer) {
        try {
            if (writer != null) {
                writer.close();
            }
        } catch(IOException ioe) {
            throw new OperationFailedException("closing output stream", ioe);
        } 
    }

    private static final Charset UNICODE = Charset.forName("UTF-8");
}
