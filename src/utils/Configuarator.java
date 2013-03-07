/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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
            File conf = new File("conf/config.xml");
            if (!conf.exists()) {
                conf = new File("src/utils/config.xml");
            }
            doc = docBuilder.parse(conf);
        } catch (SAXException ex) {
            Logger.getLogger(Configuarator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Configuarator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public HashMap<String, Integer> getPrivileges() {
        HashMap<String, Integer> mapPrivileges = new HashMap<String, Integer>();
        NodeList nl = doc.getElementsByTagName("site");
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            Element e = (Element) n;
            String siteName = e.getElementsByTagName("name").item(0).getFirstChild().getNodeValue();
            String siteP = e.getElementsByTagName("p").item(0).getFirstChild().getNodeValue();
            mapPrivileges.put(siteName, new Integer(siteP));
//            System.out.println("name: " + siteName);
//            System.out.println("p: " + siteP);
        }
        return mapPrivileges;
    }

    public String getSiteNum() {
        NodeList nl = doc.getElementsByTagName("siteNum");
        return nl.item(0).getFirstChild().getNodeValue();

    }

//    public String getLoginPort() {
//        NodeList nl = doc.getElementsByTagName("loginPort");
//        return nl.item(0).getFirstChild().getNodeValue();
//    }
//    public String getMessagePort() {
//        NodeList nl = doc.getElementsByTagName("messagePort");
//        return nl.item(0).getFirstChild().getNodeValue();
//    }
    public String getBusinessBoardIP() {
        NodeList nl = doc.getElementsByTagName("businessBoardIP");
        return nl.item(0).getFirstChild().getNodeValue();
    }

    public String getBusinessBoardPort() {
        NodeList nl = doc.getElementsByTagName("businessBoardPort");
        return nl.item(0).getFirstChild().getNodeValue();
    }

    public String getRemoteIP() {
        NodeList nl = doc.getElementsByTagName("remoteIP");
        return nl.item(0).getFirstChild().getNodeValue();
    }

    public String getRemotePort() {
        NodeList nl = doc.getElementsByTagName("remotePort");
        return nl.item(0).getFirstChild().getNodeValue();
    }

    public String getLocalPort() {
        NodeList nl = doc.getElementsByTagName("localPort");
        return nl.item(0).getFirstChild().getNodeValue();
    }
//    public static void main(String[] args) {
//        Configuarator conf = new Configuarator();
//        System.out.println(conf.getSiteNum());
//        HashMap<String, Integer> map = conf.getPrivileges();
//        for(String s:map.keySet()){
//            System.out.println("name: "+s+" p:"+map.get(s));
//        }
//    }
}
