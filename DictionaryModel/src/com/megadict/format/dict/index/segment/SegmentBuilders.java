package com.megadict.format.dict.index.segment;

import java.io.File;

public class SegmentBuilders {
    
    public static SegmentBuilder newSegmentBuilder(File indexFile) {
        return new ByteBufferedSegmentBuilder(indexFile);
    }
    
    public static SegmentBuilder newCharBufferedBuilder(File indexFile) {
        return new CharBufferedSegmentBuilder(indexFile);
    }
}
