package com.megadict.format.dict.index;

import static org.junit.Assert.*;
import org.junit.*;

import com.megadict.format.dict.index.Index;
import com.megadict.format.dict.index.IndexFile;

public class IndexStoreTest {
    
    @Ignore ("Still don't know how to create mock for this test")
    @Test
    public void testCacheIsUsed() {
        fail("not yet implemeted");
    }
    
    @Test
    public void testNewImplementation() {
        IndexFile indexFile = IndexFile.makeFile("C:/test/av.index");
        
        IndexStore store = IndexStores.newIndexStoreSupportSegment(indexFile);
        
        Index index = store.getIndexOf("person");
        
        System.out.println(index);
        
    }
}
