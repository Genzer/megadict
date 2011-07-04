package com.megadict.format.dict.sample.index;

public class ForaDictIndex implements IndexFileSample {

    @Override
    public String getFilePath() {
        return "C:\\test\\fora\\dict.index";
    }

    @Override
    public int getTotalIndexes() {
        return 387517;
    }

    @Override
    public String getSampleIndexString() {
        return "zouave\tCqnOV\tBc";
    }

    @Override
    public String getSampleHeadWord() {
        return "zouave";
    }

}
