package com.megadict.format.dict;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import org.junit.*;

import com.megadict.format.dict.index.IndexFile;
import com.megadict.format.dict.reader.DictionaryFile;
import com.megadict.model.Dictionary;
import com.megadict.test.toolbox.TaskExecutor;

public class DictionaryGetNameConcurrent {

    @Test
    public void testDictionaryWithSegment() {
        IndexFile veIndex = IndexFile.makeFile("C:/test/ve.index");
        DictionaryFile veDict = DictionaryFile.makeRandomAccessFile("C:/test/ve.dict");
        
        IndexFile evIndex = IndexFile.makeFile("C:/test/av.index");
        DictionaryFile evDict = DictionaryFile.makeRandomAccessFile("C:/test/av.dict");
        
        IndexFile foraIndex = IndexFile.makeFile("C:/test/fora.index");
        DictionaryFile foraDict = DictionaryFile.makeRandomAccessFile("C:/test/fora.dict");
        
        GetNameTask[] tasks = {
                new GetNameTask(veIndex, veDict),
                new GetNameTask(evIndex, evDict),
                new GetNameTask(foraIndex, foraDict),
        };
        
        List<String> names = TaskExecutor.executeAndGetResult(Arrays.asList(tasks));

        for (String name : names) {
            assertNotNull(name);
            System.out.println(name);
        }
    }
    
    
    private static class GetNameTask implements Callable<String> {

        public GetNameTask(IndexFile indexFile, DictionaryFile dictFile) {
            this.indexFile = indexFile;
            this.dictFile = dictFile;
        }
        
        
        @Override
        public String call() throws Exception {
            Dictionary dictionary = 
                new DICTDictionary.Builder(indexFile, dictFile).enableSplittingIndexFile().build();
            return dictionary.getName();
        }
        
        private final IndexFile indexFile;
        private final DictionaryFile dictFile;
    }

}
