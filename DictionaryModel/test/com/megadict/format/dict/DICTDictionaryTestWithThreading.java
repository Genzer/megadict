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

       LookUpAndPrintNameTask[] tasks = {
               new LookUpAndPrintNameTask(packOneIndex, packOneDict, "something"),
               new LookUpAndPrintNameTask(packTwoIndex, packTwoDict, "something"),
               
       };
       
       TaskExecutor.execute(Arrays.asList(tasks));       

    }

    @Ignore
    @Test
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

    static class LookUpAndPrintNameTask implements Runnable {

        private IndexFile indexFile;
        private DictionaryFile dictFile;
        private String testWord;
        
        public LookUpAndPrintNameTask(IndexFile indexFile, DictionaryFile dictFile, String testWord) {
            this.indexFile = indexFile;
            this.dictFile = dictFile;
            this.testWord = testWord;
        }

        @Override
        public void run() {
            Dictionary dictionary = new DICTDictionary.Builder(indexFile, dictFile)
                    .enableSplittingIndexFile().build();
            
            Definition def = dictionary.lookUp(testWord);
            System.out.println(def.getWord());
            System.out.println(dictionary.getName());
        }

    }

}
