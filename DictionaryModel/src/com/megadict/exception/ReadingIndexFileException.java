package exception;

public class ReadingIndexFileException extends RuntimeException {

    public ReadingIndexFileException(String indexFile, Exception rootCause) {
        super(makeMessage(indexFile), rootCause);
    }
    
    private static String makeMessage(String indexFile) {
        return "Problem occured while reading index file " + indexFile ;
    }

    private static final long serialVersionUID = 2043393140848800452L;
}
