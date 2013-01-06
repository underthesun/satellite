/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author shuai
 */
public class Configuarator {

    private DocumentBuilderFactory factory;
    private DocumentBuilder docBuilder;
    private Document doc;

    public Configuarator() {
        factory = DocumentBuilderFactory.newInstance();
        try {
            docBuilder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Configuarator.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            doc = docBuilder.parse(new File("conf/test.xml"));
        } catch (SAXException ex) {
            Logger.getLogger(Configuarator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Configuarator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String getSiteNum(){
        NodeList nl = doc.getElementsByTagName("siteNum");
//        System.out.println(nl.toString());
        String num = nl.item(0).getFirstChild().getNodeValue();
        return num;
    }
    
//    public String getSiteNum(){
//        NodeList nl = doc.getElementsByTagName("employee");
//            for(int i=0; i<nl.getLength();i++){
//                Node n = nl.item(i);
//                if(n.getNodeType() == Node.ELEMENT_NODE){
//                    Element e = (Element) n;
//                    System.out.println("name: " + e.getElementsByTagName("name").item(0).getFirstChild().getNodeValue());
//                    System.out.println("gender: "+e.getElementsByTagName("gender").item(0).getFirstChild().getNodeValue());
//                    System.out.println("age: "+e.getElementsByTagName("age").item(0).getFirstChild().getNodeValue());
//                }
//            }
//    }
    
    public static void main(String[] args){
        System.out.println(new Configuarator().getSiteNum());
    }
}
