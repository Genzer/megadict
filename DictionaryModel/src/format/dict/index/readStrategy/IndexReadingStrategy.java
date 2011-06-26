package format.dict.index.readStrategy;

import java.io.IOException;
import java.util.List;

public interface IndexReadingStrategy {
    List<String> readFullContent() throws IOException;
}
