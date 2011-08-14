package com.megadict.format.dict.index;

import java.io.File;
import java.util.Set;

import com.megadict.format.dict.index.segment.ByteBufferedSegmentBuilder;
import com.megadict.format.dict.index.segment.SegmentBuilder;
import com.megadict.format.dict.index.segment.SegmentStore;

class IndexStoreWithSegmentSupport extends BaseIndexStore implements IndexStore {

    public IndexStoreWithSegmentSupport(IndexFile indexFile) {
        super(indexFile);
        buildSegments();
    }  

    private void buildSegments() {
        SegmentBuilder builder = makeSegmentBuilder();
        if (builder.checkIfSegmentIndexExists()) {
            builder.loadSavedSegmentIndex();
        } else {
            builder.build();
            builder.saveSegmentIndex();
        }        
        segmentStore = new SegmentStore(builder.builtSegments());
    }
    
    private SegmentBuilder makeSegmentBuilder() {
        return new ByteBufferedSegmentBuilder(indexFile.asRawFile());
    }
    
    @Override
    protected Set<Index> findInFile(String word) {
        File segmentFile = determineWhichSegmentContains(word);
        IndexFileReader reader = new IndexFileReader(segmentFile);
        return reader.getIndexesStartFrom(word);
    }
    
    private File determineWhichSegmentContains(String word) {
        return segmentStore.findSegmentPossiblyContains(word).file();
    }
    
    private SegmentStore segmentStore;
}
