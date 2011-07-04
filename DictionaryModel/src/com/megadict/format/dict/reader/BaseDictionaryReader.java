package com.megadict.format.dict.reader;

import java.io.*;

import com.megadict.exception.ResourceMissingException;
import com.megadict.format.dict.index.Index;

abstract class BaseDictionaryReader implements DictionaryReader{
    
    public BaseDictionaryReader(String dictionaryFile) {
        this.dictionaryFile = dictionaryFile;
    }
    
    public String getDefinitionByIndex(Index index) {
        byte[] contentInBytes = readDictionaryFile(index);
        return newUnicodeStringFrom(contentInBytes);
    }

    private byte[] readDictionaryFile(Index index) {
        try {
            openFileInReadOnlyMode();
            return readContentAtIndex(index);
        } catch (FileNotFoundException fnf) {
            throw new ResourceMissingException(this.dictionaryFile, fnf);
        } catch (IOException ioe) {
            throw new RuntimeException("Cannot read dictionary file", ioe);
        } finally {
            cleanUp();
        }
    }

    private void openFileInReadOnlyMode() throws FileNotFoundException {
        fileReader = constructFileReader();
    }
    
    abstract DictFileReader constructFileReader() throws FileNotFoundException ;

    private byte[] readContentAtIndex(Index index) throws IOException {
        byte[] content = new byte[index.getByteLenght()];
        fileReader.read(content, index.getByteOffset(), index.getByteLenght());
        return content;
    }

    private void cleanUp() {
        try {
            closeReader();
        } catch (IOException e) {
            throw new RuntimeException("Cannot close " + this.dictionaryFile
                    + ".", e);
        }
    }

    private void closeReader() throws IOException {
        fileReader.close();
    }

    public static String newUnicodeStringFrom(byte[] octets) {
        String unicodeString = null;
        try {
            unicodeString = new String(octets, CHARSET_UNICODE);
        } catch (UnsupportedEncodingException e) {
            /*
             * We can leave this catch block empty because the exception will
             * never be thrown. Every implementation of Java Platforms are
             * required to support UTF-8 charset.
             */
        }
        return unicodeString;
    }
    
    final String dictionaryFile;
    private static final String CHARSET_UNICODE = "UTF-8";
    private DictFileReader fileReader;
}
