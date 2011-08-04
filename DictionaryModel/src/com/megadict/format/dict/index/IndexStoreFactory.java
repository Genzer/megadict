package com.megadict.format.dict.index;

public class IndexStoreFactory {
    
    private IndexStoreFactory() {
    }

    public static IndexStore newDefaultIndexStore(IndexFile indexFile) {
        return new BaseIndexStore(indexFile);
    }

    public static IndexStore newIndexStoreSupportSegment(IndexFile indexFile) {
        return new IndexStoreWithSegmentSupport(indexFile);
    }
}
