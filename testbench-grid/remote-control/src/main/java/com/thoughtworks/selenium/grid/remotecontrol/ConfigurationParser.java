package com.thoughtworks.selenium.grid.remotecontrol;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ConfigurationParser {

    public static void parseConfigurationFile(OptionParser.Options options){
        
        File configuration = new File("rc_configuration.xml");
        if(!configuration.exists()){
            return;
        }
        
        try{
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(configuration);
            doc.getDocumentElement().normalize();
            // Get <host> node and set title if exists
            NodeList nodeList = doc.getElementsByTagName("host");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if(node.getChildNodes().item(0).getNodeValue() != null){
                    options.setHost(node.getChildNodes().item(0).getNodeValue());
                }
            }
            
            // Get <hubURL> node and set hubURL if exists
            nodeList = doc.getElementsByTagName("hubURL");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                String hubURL = node.getChildNodes().item(0).getNodeValue();
                // Add http:// if hubURL doesn't start with it
                if(!hubURL.startsWith("http://")){
                    hubURL = "http://" + hubURL;
                }
                // Add port if not available
                if(hubURL.split(":").length != 3){
                    System.err.println("Host missing port.\nAdding default port 4444");
                    hubURL = hubURL + ":4444";
                }
                options.setHubURL(hubURL);
            }
    
            // Get <port> node and set port if exists
            nodeList = doc.getElementsByTagName("port");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                options.setPort(node.getChildNodes().item(0).getNodeValue());
            }
    
            StringBuilder sb = new StringBuilder();
            // Get all <environment> nodes and create environment string
            nodeList = doc.getElementsByTagName("environment");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                sb.append(node.getChildNodes().item(0).getNodeValue()).append(",");
            }
            if(sb.length() > 0){
                // Remove last ',' from string
                sb.deleteCharAt(sb.lastIndexOf(","));
                // Set enviroments
                options.setEnvironment(sb.toString());
            }
            
        }catch(javax.xml.parsers.ParserConfigurationException pce){
            System.err.println("Error in configuration file rc_configuration.xml\nUsing arguments or default values.");
            return;
        }catch(org.xml.sax.SAXException saxe){
            System.err.println("Error in configuration file rc_configuration.xml\nUsing arguments or default values.");
            return;
        }catch(java.io.IOException ioe){
            System.err.println("Error in configuration file rc_configuration.xml\nUsing arguments or default values.");
            return;
        }
    }
}
