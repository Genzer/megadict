package com.megadict.format.dict;

import org.junit.Test;
import static org.junit.Assert.*;

import com.megadict.format.dict.index.IndexFile;
import com.megadict.format.dict.reader.DictionaryFile;
import com.megadict.model.Definition;
import com.megadict.model.Dictionary;

public class DICTDictionaryTestWithThreading {

    @Test
    public void testTwoDictionarySimultaneously() {
       IndexFile packOneIndex =
           IndexFile.makeFile("D:/Workspace/@MainProjects/OU/MegaDict/Project/DictionaryModel/testset/hnd/ev/ev.index");
       DictionaryFile packOneDict = 
           DictionaryFile.makeRandomAccessFile("D:/Workspace/@MainProjects/OU/MegaDict/Project/DictionaryModel/testset/hnd/ev/ev.dict");
       
       IndexFile packTwoIndex =
           IndexFile.makeFile("D:/Workspace/@MainProjects/OU/MegaDict/Project/DictionaryModel/testset/hnd/ve/ve.index");
       DictionaryFile packTwoDict = 
           DictionaryFile.makeRandomAccessFile("D:/Workspace/@MainProjects/OU/MegaDict/Project/DictionaryModel/testset/hnd/ve/ve.dict");
       
       Thread packOne = new GetDictionaryName(packOneIndex, packOneDict);
       Thread packTwo = new GetDictionaryName(packTwoIndex, packTwoDict);
       packOne.start();
       packTwo.start();
    }

    private static class GetDictionaryName extends Thread {

        public GetDictionaryName(IndexFile indexFile, DictionaryFile dictFile) {
            this.dictFile = dictFile;
            this.indexFile = indexFile;
        }

        @Override
        public void run() {
            Dictionary dictionary = new DICTDictionary(indexFile, dictFile);
            Definition def = dictionary.lookUp("00-database-short");
            System.out.println(def.getContent());
        }

        private final IndexFile indexFile;
        private final DictionaryFile dictFile;

    }

}
