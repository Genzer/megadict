package com.megadict.test.toolbox.samples;

import java.io.*;
import java.util.List;

import org.jdom.*;
import org.jdom.input.SAXBuilder;

public class XmlDocumentReader {
    
    public XmlDocumentReader(File xmlDocument) {
        loadContent(xmlDocument);
    }
    
    private void loadContent(File document) {
        SAXBuilder builder = new SAXBuilder();
        try {
            content = builder.build(document);
        } catch (JDOMException jdom) {
          handleException(jdom);
        } catch (IOException ioe) {
          handleException(ioe);
        }
    }
    
    private static void handleException(Exception ex) {
        throw new RuntimeException("Problem occured while building XML document.", ex);
    }
    
    public String getValueOf(String pathToElement) {
        return getElementAt(pathToElement).getText();
    }
        
    public Element getElementAt(String pathToElement) {
        return getAllElementsAt(pathToElement).get(0);
    }
    
    @SuppressWarnings("unchecked")
    public List<Element> getAllElementsAt(String pathToElements) {
        String[] elementNames = pathToElements.split("/");
        
        Element element = content.getRootElement();
        
        int length = elementNames.length - 1;
        
        for (int position = 1; position < length; position++) {
            element = element.getChild(elementNames[position]);
        }
        
        return element.getChildren(elementNames[length]);
    }
        
    private Document content;
}
