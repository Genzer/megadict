package com.megadict.format.dict.sample.index;

public class PackageIncluded implements IndexFileSample {

    public String getFilePath() {
        return PackageIncluded.class.getResource("/format/dict/sample/index/sampleIndexFile.index").getFile();
    }

    public int getTotalIndexes() {
        return 100;
    }

    public String getSampleIndexString() {
        return "abaci\t9x\tDb";
    }

    public String getSampleHeadWord() {
        return "abaci";
    }

}
