package com.megadict.test.toolbox.samples;

import java.io.File;


public class TestSamples {
    
    private static final String HND_ENG_VI_PATH = 
        "D:/Workspace/@MainProjects/OU/MegaDict/Project/DictionaryModel/testset/hnd/ev/profile-unit-test.xml";
    
    private static final DictionaryTestSample HND_ENG_VI = new DefaultUnitTestProfile(new File(HND_ENG_VI_PATH));
    
//    private static final String HND_VI_ENG_PATH = 
//        "D:/Workspace/@MainProjects/OU/MegaDict/Project/DictionaryModel/testset/hnd/ve/profile-unit-test.xml";
    
    private static final DictionaryTestSample HND_VI_ENG = null;
    
    private static final String FORA_ENG_VI_PATH =
        "D:/Workspace/@MainProjects/OU/MegaDict/Project/DictionaryModel/testset/fora/ev/profile-unit-test.xml";
    
    private static final DictionaryTestSample FORA_ENG_VI = new DefaultUnitTestProfile(new File(FORA_ENG_VI_PATH));
    
    public static DictionaryTestSample getCurrentDictionarySample() {
        return FORA_ENG_VI;
    }
    
    public static IndexTestSample getCurrentIndexSample() {
        return (IndexTestSample) FORA_ENG_VI;
    }
    
    public static DictionaryTestSample getHndEngViDictionaryTest() {
        return HND_ENG_VI;
    }
    
    public static DictionaryTestSample getHndViEngDictionaryTest() {
        return HND_VI_ENG;
    }    
}
