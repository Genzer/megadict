package com.megadict.format.dict.sample.index;

public class Thesaurus implements IndexFileSample {

    public String getFilePath() {
        return "C:\\test\\dictd\\moby-thesaurus.index";
    }

    public int getTotalIndexes() {
        return 30263;
    }

    public String getSampleIndexString() {
        return "00-database-short\tBZ\t8";
    }

    public String getSampleHeadWord() {
        return "00-database-short";
    }

}
