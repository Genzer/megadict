package com.megadict.test.toolbox.samples;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;

class SampleWordLoader {

    public SampleWordLoader(File sampleFile) {
        this.sampleFile = sampleFile;
    }

    public Map<String, Sample> loadContent() {
        XmlDocumentReader reader = new XmlDocumentReader(sampleFile);

        List<Element> samples = reader.getAllElementsAt("samples/sample");

        Map<String, Sample> sampleWords = new HashMap<String, Sample>(
                samples.size());

        for (Element sample : samples) {
            String headWord = sample.getChildText("headword");
            String indexString = sample.getChildText("indexString");
            String expectedContent = sample.getChildText("expectedContent");
            Sample newSample = new Sample(headWord, indexString, expectedContent);
            sampleWords.put(newSample.getHeadWord(), newSample);
        }
        
        return sampleWords;
    }

    private File sampleFile;
}
