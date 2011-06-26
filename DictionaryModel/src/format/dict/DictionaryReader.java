package format.dict;

import java.io.*;

import exception.ResourceMissingException;
import format.dict.index.Index;

class DictionaryReader {

    public DictionaryReader(String dictionaryFile) {
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
        fileReader = new RandomAccessFile(this.dictionaryFile, "r");
    }
    
    private byte[] readContentAtIndex(Index index) throws IOException {
        seekToOffset(index.getByteOffset());
        return readBytesWithAmountOf(index.getByteLenght());
    }
    
    private void seekToOffset(int offset) throws IOException {
        fileReader.seek(offset);
    }
    
    private byte[] readBytesWithAmountOf(int length) throws IOException {
        byte[] content = new byte[length];
        fileReader.read(content, 0, length);
        return content;
    }
    
    private void cleanUp() {
        try {
            closeReader();
        } catch (IOException e) {
            throw new RuntimeException("Cannot close " + this.dictionaryFile + ".", e);
        }
    }
    
    private void closeReader() throws IOException {
        fileReader.close();
    }

    private String newUnicodeStringFrom(byte[] octets) {
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

    private final String dictionaryFile;
    private RandomAccessFile fileReader;
    private static final String CHARSET_UNICODE = "UTF-8";

}
