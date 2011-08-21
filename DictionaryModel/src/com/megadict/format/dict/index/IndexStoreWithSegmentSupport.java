package com.megadict.format.dict.index;

import java.util.Set;

import com.megadict.format.dict.index.segment.ByteBufferedSegmentIndexer;
import com.megadict.format.dict.index.segment.SegmentBuilder;
import com.megadict.format.dict.index.segment.SegmentStore;

class IndexStoreWithSegmentSupport extends BaseIndexStore implements IndexStore {
   
    private final IndexFileReader reader;
    
    public IndexStoreWithSegmentSupport(IndexFile indexFile) {
        super(indexFile);        
        reader = buildReader();
    }  
    
    private IndexFileReader buildReader() {
        SegmentStore segmentStore = buildSegments();
        return new RandomIndexFileReader(indexFile.asRawFile(), segmentStore);
    }

    private SegmentStore buildSegments() {
        SegmentBuilder builder = new ByteBufferedSegmentIndexer(indexFile.asRawFile());
        builder.build();
        return new SegmentStore(builder.builtSegments());
    }
    
    @Override
    protected Set<Index> findInFile(String word) {
        return reader.getIndexesSurrounding(word);
    }
}
