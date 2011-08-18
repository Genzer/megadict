package com.megadict.format.dict.index.segment;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;

public class ByteBufferedSegmentIndexerTest {

    // @Ignore("Imcomplete implementation")
    @Test
    public void testBuild() {
        File hndIndex = new File("C:/test/av.index");
        File foraIndex = new File("C:/test/fora.index");
        
        BuildSegment[] tasks = {
                new BuildSegment(hndIndex),
                new BuildSegment(foraIndex),
        };
        
        ExecutorService executor = Executors.newFixedThreadPool(2);
        List<Future<List<Segment>>> results = new ArrayList<Future<List<Segment>>>();
        try {
            results = executor.invokeAll(Arrays.asList(tasks));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        
        
        for (Future<List<Segment>> result : results) {
            try {
                List<Segment> segments = result.get();
                printSegmentAt(segments, 0);
                printSegmentAt(segments, segments.size() - 1);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
       
    }

    private void printSegmentAt(List<Segment> segments, int index) {
        Segment segment = segments.get(index);
        System.out.println(segment);
        System.out.println();
    }
    
    private static class BuildSegment implements Callable<List<Segment>> {

        public BuildSegment(File indexFile) {
            this.indexFile = indexFile;
        }
        
        @Override
        public List<Segment> call() throws Exception {
            SegmentBuilder builder = new ByteBufferedSegmentIndexer(indexFile);
            builder.build();
            return builder.builtSegments();
        }
        
        private File indexFile;
    }

}
