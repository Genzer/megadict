package com.megadict.format.dict.sample;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;

class SampleWordLoader {

    public SampleWordLoader(File sampleFile) {
        this.sampleFile = sampleFile;
    }

    public Map<String, String> loadContent() {
        XmlDocumentReader reader = new XmlDocumentReader(sampleFile);

        List<Element> samples = reader.getAllElementsAt("samples/sample");

        Map<String, String> sampleWords = new HashMap<String, String>(
                samples.size());

        for (Element sample : samples) {
            String headWord = sample.getChildText("headword");
            String expectedContent = sample.getChildText("expectedContent");
            sampleWords.put(headWord, expectedContent);
        }
        
        return sampleWords;
    }

    private File sampleFile;
}
