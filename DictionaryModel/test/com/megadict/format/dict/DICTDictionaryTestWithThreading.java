package com.megadict.format.dict;

import java.util.*;
import java.util.concurrent.*;

import org.junit.*;

import com.megadict.format.dict.index.IndexFile;
import com.megadict.format.dict.reader.DictionaryFile;
import com.megadict.model.Definition;
import com.megadict.model.Dictionary;
import com.megadict.test.toolbox.TaskExecutor;

public class DICTDictionaryTestWithThreading {

    private static final IndexFile packOneIndex = IndexFile
            .makeFile("D:/Workspace/@MainProjects/OU/MegaDict/Project/DictionaryModel/testset/hnd/ev/ev.index");
    private static final DictionaryFile packOneDict = DictionaryFile
            .makeRandomAccessFile("D:/Workspace/@MainProjects/OU/MegaDict/Project/DictionaryModel/testset/hnd/ev/ev.dict");

    private static final IndexFile packTwoIndex = IndexFile
            .makeFile("D:/Workspace/@MainProjects/OU/MegaDict/Project/DictionaryModel/testset/hnd/ve/ve.index");
    private static final DictionaryFile packTwoDict = DictionaryFile
            .makeRandomAccessFile("D:/Workspace/@MainProjects/OU/MegaDict/Project/DictionaryModel/testset/hnd/ve/ve.dict");

    @Test
    public void testSimultaneouslyLookUpWithTwoDictionaries() {
        
        Dictionary dictionary = new DICTDictionary.Builder(packOneIndex, packOneDict).enableSplittingIndexFile().build();
        
        LookUpTask lookUpTask = new LookUpTask(dictionary, "zoom");
        RecommendTask recTask = new RecommendTask(dictionary, "zoom");

        ExecutorService exe = Executors.newFixedThreadPool(2);
        try {
            Future<String> resultLookUp = exe.submit(lookUpTask);
            Future<List<String>> resultRecommend = exe.submit(recTask);

            //exe.awaitTermination(200, TimeUnit.MILLISECONDS);

            System.out.println(resultLookUp.get());
            System.out.println(resultRecommend.get());
        } catch (InterruptedException ie) {
            throw new RuntimeException(ie);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            exe.shutdown();
        }

    }

    @Ignore @Test
    public void testSimultaneouslyGetNameOfTwoDictionaries() {
        List<Runnable> tasks = new ArrayList<Runnable>(2);
        tasks.add(new GetNameTask(packOneIndex, packOneDict));
        tasks.add(new GetNameTask(packTwoIndex, packTwoDict));

        TaskExecutor.execute(tasks);
    }

    static class GetNameTask implements Runnable {

        public GetNameTask(IndexFile indexFile, DictionaryFile dictFile) {
            this.dictFile = dictFile;
            this.indexFile = indexFile;
        }

        @Override
        public void run() {
            Dictionary dictionary =
                    new DICTDictionary.Builder(indexFile, dictFile).build();
            System.out.println(dictionary.getName());
        }

        private final IndexFile indexFile;
        private final DictionaryFile dictFile;

    }

    static class LookUpTask implements Callable<String> {
        
        public LookUpTask(Dictionary dictionary, String wordToLookUp) {
            this.dict = dictionary;
            this.wordToLookUp = wordToLookUp;
        }

        @Override
        public String call() throws Exception {
            Definition def = dict.lookUp(wordToLookUp);
            return def.getContent();
        }

        private final Dictionary dict;
        private final String wordToLookUp;
    }

    static class RecommendTask implements Callable<List<String>> {
        
        public RecommendTask(Dictionary dictionary, String wordToLookUp) {
            this.dict = dictionary;
            this.wordToLookUp = wordToLookUp;
        }
        
        @Override
        public List<String> call() throws Exception {
            List<String> words = dict.recommendWord(wordToLookUp);
            return words;
        }
        
        private Dictionary dict;
        private final String wordToLookUp;
    }
}
