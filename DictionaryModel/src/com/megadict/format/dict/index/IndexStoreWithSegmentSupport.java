package com.megadict.format.dict.index;

import java.io.File;
import java.util.Set;

class IndexStoreWithSegmentSupport extends BaseIndexStore implements IndexStore {

    public IndexStoreWithSegmentSupport(IndexFile indexFile) {
        super(indexFile);
        buildSegments();
    }  

    private void buildSegments() {
        SegmentBuilder builder = new CustomBufferedSegmentBuilder(indexFile.asRawFile());
        if (builder.findSegmentMainIndexIfExists()) {
            builder.loadSavedSegmentMainIndex();
        } else {
            builder.build();
            builder.saveSegmentMainIndex();
        }        
        segmentStore = new SegmentStore(builder.builtSegments());
    }
    
    @Override
    protected Set<Index> findInFile(String word) {
        File segmentFile = determineWhichSegmentContains(word);
        IndexFileReader reader = new IndexFileReader(segmentFile);
        return reader.getIndexesStartFrom(word);
    }
    
    private File determineWhichSegmentContains(String word) {
        return segmentStore.getSegmentContains(word).file();
    }
    
    private SegmentStore segmentStore;
}
