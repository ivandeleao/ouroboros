/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 *
 * @author ivand
 */
public class MwXML {
    
    public static void createFile(Document document, String path){
    
        DOMSource source = new DOMSource(document);
        File f= new File(path);
        //create result stream
        Result result = new StreamResult(f);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            try {
                transformer.transform(source, result);
            } catch (TransformerException ex) {
                Logger.getLogger(MwXML.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.print("arquivo xml gerado");
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(MwXML.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static String elementToString(Element element){
        TransformerFactory transformerFactory = TransformerFactory
                .newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(MwXML.class.getName()).log(Level.SEVERE, null, ex);
        }
        DOMSource source = new DOMSource(element);
        StreamResult result = new StreamResult(new StringWriter());

        try {
            transformer.transform(source, result);
        } catch (TransformerException ex) {
            Logger.getLogger(MwXML.class.getName()).log(Level.SEVERE, null, ex);
        }

        String strObject = result.getWriter().toString();
        
        return strObject;
    }
    
    public static Element createElement(Document document, String name, String value){
        Element e = document.createElement(name);
        e.appendChild(document.createTextNode(value));
        return e;
    }
    
    public static Document convertStringToDocument(String xmlStr) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
        DocumentBuilder builder;  
        try  
        {  
            builder = factory.newDocumentBuilder();  
            Document doc = builder.parse( new InputSource( new StringReader( xmlStr ) ) ); 
            return doc;
        } catch (Exception e) {  
            e.printStackTrace();  
        } 
        return null;
    }

    /**
     * 
     * @param xml
     * @param tag
     * @return value from the first tag found
     */
    public static String getText(Document xml, String tag){
        
        NodeList nodes = xml.getElementsByTagName(tag);

        //System.out.println(nodes.getLength());

        if(nodes.getLength() > 0){
            return nodes.item(0).getTextContent();
        }
        
        return null;
    }
    
    /**
     * 
     * @param xml
     * @param parentTag
     * @param tag
     * @return value from the first tag found inside parentTag
     */
    public static String getText(Document xml, String parentTag, String tag){
        NodeList nodes = xml.getElementsByTagName(parentTag);
        //System.out.println(nodes.getLength());

        for(int j = 0; j < nodes.getLength();j++){
            Node temp = nodes.item(j);
            NodeList children = temp.getChildNodes();

            for(int k = 0; k < children.getLength(); k++){
                if(children.item(k).getNodeName().equals(tag)){
                    //System.out.print(children.item(k).getTextContent()+"\t");
                    return children.item(k).getTextContent();
                }
            }
        }
        
        return null;
    }
    
    /**
     * 
     * @param xml
     * @param tag
     * @param attribute
     * @return property value from the first tag found
     */
    public static String getAttributeValue(Document xml, String tag, String attribute){
        
        NodeList nodes = xml.getElementsByTagName(tag);

        if(nodes.getLength() > 0){
            return nodes.item(0).getAttributes().getNamedItem(attribute).getNodeValue();
        }
        
        return null;
    }
    
    /**
     * 
     * @param xml
     * @param parentTag
     * @return list of pairs (key=value) inside a tag 
     */
    public static List<Map<String,String>> getPairs(Document xml, String parentTag){
        
        List<Map<String,String>> list = new ArrayList<>();
        
        NodeList nodes = xml.getElementsByTagName(parentTag);

        for(int j = 0; j < nodes.getLength();j++){
            Map<String,String> map = new HashMap<>();
            Node temp = nodes.item(j);
            NodeList children = temp.getChildNodes();

            for(int k = 0; k < children.getLength(); k++){
                //'#text' is the result of invoking getNodeName() method on empty node
                if(!children.item(k).getNodeName().equals("#text")){
                    //System.out.println(children.item(k).getTextContent());
                    map.put(children.item(k).getNodeName(), children.item(k).getTextContent());
                }
            }
            list.add(map);
        }
        return list;
    }
    
    public static String convertDocumentToString(Document doc){
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            String output = writer.getBuffer().toString().replaceAll("\n|\r", "");
            
            return output;
        } catch (IllegalArgumentException | TransformerException e) {
            System.err.println(e);
        }
        return null;
    }
}
