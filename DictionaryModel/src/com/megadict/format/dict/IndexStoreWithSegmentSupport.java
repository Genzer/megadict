package com.megadict.format.dict;

import java.util.Set;

import com.megadict.format.dict.index.Index;
import com.megadict.format.dict.index.IndexFile;
import com.megadict.format.dict.index.IndexFileReader;
import com.megadict.format.dict.index.RandomIndexFileReader;
import com.megadict.format.dict.index.segment.SegmentBuilder;
import com.megadict.format.dict.index.segment.SegmentBuilders;
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
        SegmentBuilder builder = SegmentBuilders.newSegmentBuilder(indexFile.asRawFile());
        builder.build();
        return new SegmentStore(builder.builtSegments());
    }
    
    @Override
    protected Set<Index> findInFile(String word) {
        return reader.getIndexesSurrounding(word);
    }
}
