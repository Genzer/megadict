package format.dict;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class DictionaryReader {

    public DictionaryReader(String dictionaryFile) {
        this.dictionaryFile = dictionaryFile;
    }
    
    public String getDefinitionByIndex(Index index) {
        
        RandomAccessFile dictFile = null;
        
        try {
           dictFile  = new RandomAccessFile(dictionaryFile, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        //final int MAX_BUFFER = 5234023;
        
        byte[] readBytes  = new byte[index.getByteLenght()];
        //StringBuilder builder = new StringBuilder();
        try {
            dictFile.seek(index.getByteOffset());
            dictFile.readFully(readBytes, 0, index.getByteLenght());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                dictFile.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        return new String(readBytes);
        
    }
    
    private final String dictionaryFile;
    
}
