package com.megadict.format.dict.index;

public class IndexStoreFactory {
    
    private IndexStoreFactory() {
    }

    public static IndexStoreDefaultImpl newDefaultIndexStore(IndexFile indexFile) {
        return new BaseIndexStore(indexFile);
    }

    public static IndexStoreDefaultImpl newIndexStoreSupportSegment(IndexFile indexFile) {
        return new IndexStoreWithSegmentSupport(indexFile);
    }
}
