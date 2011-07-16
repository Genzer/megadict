package com.megadict.format.dict.sample;

import java.io.File;

public class TestSamples {
    
    private static final String HND_ENG_VI_PATH = 
        "D:/Workspace/@MainProjects/OU/MegaDict/Project/DictionaryModel/testset/hnd/ev/profile-unit-test.xml";
    
    private static final DictionaryTestSample HND_ENG_VI = new DefaultUnitTestProfile(new File(HND_ENG_VI_PATH));
    
//    private static final String HND_VI_ENG_PATH = 
//        "D:/Workspace/@MainProjects/OU/MegaDict/Project/DictionaryModel/testset/hnd/ve/profile-unit-test.xml";
    
    private static final DictionaryTestSample HND_VI_ENG = null;
    
    public static DictionaryTestSample getCurrentDictionarySample() {
        return HND_ENG_VI;
    }
    
    public static IndexTestSample getCurrentIndexSample() {
        return (IndexTestSample) HND_ENG_VI;
    }
    
    public static DictionaryTestSample getHndEngViDictionaryTest() {
        return HND_ENG_VI;
    }
    
    public static DictionaryTestSample getHndViEngDictionaryTest() {
        return HND_VI_ENG;
    }    
}
