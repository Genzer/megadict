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

        List<Callable<String>> tasks = new ArrayList<Callable<String>>(2);
        tasks.add(new LookUpTask(packOneIndex, packOneDict, "b"));
        tasks.add(new LookUpTask(packTwoIndex, packTwoDict, "ông"));

        List<String> results = TaskExecutor.executeAndGetResult(tasks);

        for (String result : results) {
            System.out.println(result);
        }
    }

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
                    new DICTDictionary(indexFile, dictFile);
            System.out.println(dictionary.getName());
        }

        private final IndexFile indexFile;
        private final DictionaryFile dictFile;

    }

    static class LookUpTask implements Callable<String> {

        public LookUpTask(IndexFile indexFile, DictionaryFile dictFile, String wordToLookUp) {
            this.dictFile = dictFile;
            this.indexFile = indexFile;
            this.wordToLookUp = wordToLookUp;
        }

        @Override
        public String call() throws Exception {
            Dictionary dictionary = new DICTDictionary(indexFile, dictFile);
            Definition def = dictionary.lookUp(wordToLookUp);
            return def.getContent();
        }

        private final IndexFile indexFile;
        private final DictionaryFile dictFile;
        private final String wordToLookUp;
    }
}
