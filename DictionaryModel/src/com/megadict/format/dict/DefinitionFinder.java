package com.megadict.format.dict;

import java.io.UnsupportedEncodingException;

import com.megadict.format.dict.index.Index;
import com.megadict.format.dict.reader.DictFileReader;
import com.megadict.model.Definition;

public class DefinitionFinder {
    
    public DefinitionFinder(DictFileReader reader) {
        this.fileReader = reader;
    }    
    
    public String getDefinitionAt(Index index) {
        if (index == null) {
            return "";
        }
        byte[] contentInBytes = readDictionaryFile(index);
        return newUnicodeStringFrom(contentInBytes);
    }

    private byte[] readDictionaryFile(Index index) {
        openFileInReadOnlyMode();
        byte[] content = readContentAtIndex(index);
        cleanUp();
        return content;
    }

    private void openFileInReadOnlyMode() {
        fileReader.open();
    }

    private byte[] readContentAtIndex(Index index) {
        return fileReader.read(index.getByteOffset(), index.getByteLength());
    }

    private void cleanUp() {
        fileReader.close();
    }

    private static String newUnicodeStringFrom(byte[] octets) {
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
    
    private static final String CHARSET_UNICODE = "UTF-8";
    private DictFileReader fileReader;
}
