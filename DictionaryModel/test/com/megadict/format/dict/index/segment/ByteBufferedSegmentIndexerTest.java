package com.megadict.format.dict.index.segment;

import static org.junit.Assert.*;

import java.io.File;
import java.util.*;
import java.util.concurrent.Callable;

import org.junit.*;

import com.megadict.test.toolbox.TaskExecutor;

public class ByteBufferedSegmentIndexerTest {

    private static class BuildSegmentTask implements Callable<List<Segment>> {

        public BuildSegmentTask(File indexFile) {
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
    
    @Test
    public void testBuildSegmentWithMultithread() {
        File hndIndex = new File("C:/test/av.index");
        File foraIndex = new File("C:/test/fora.index");

        BuildSegmentTask[] tasks = {
                new BuildSegmentTask(hndIndex),
                new BuildSegmentTask(foraIndex),
        };

        List<List<Segment>> results = TaskExecutor.executeAndGetResult(Arrays.asList(tasks));

        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        for (List<Segment> result : results) {
            assertFalse(result.isEmpty());
        }
    }

    @Ignore @Test
    public void testSerializeSegments() {
        File indexFile = new File("C:/test/fora.index");
        SegmentBuilder builder = new ByteBufferedSegmentIndexer(indexFile);
        builder.build();
        
        List<Segment> segments = builder.builtSegments();
        
        File segmentIndexFile = new File("C:/test/foraIndexFile.sif");
        SegmentIndexWriter writer = new SegmentIndexWriter(segmentIndexFile, segments);
        writer.write();
    }
    
    @Ignore @Test
    public void testDeserializeSegments() {
        File serializedIndex = new File("C:/test/foraIndexFileTestRead.sif");
        SegmentIndexReader reader = new SegmentIndexReader(serializedIndex);
        List<Segment> segments = reader.read();
        assertNotNull(segments);
        assertFalse(segments.isEmpty());
    }

}
