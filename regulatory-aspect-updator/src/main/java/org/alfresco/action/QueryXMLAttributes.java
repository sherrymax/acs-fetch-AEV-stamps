package org.alfresco.action;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;

public class QueryXMLAttributes {

    public ArrayList<String> getSubjectFromStamp(InputStream inputStream) {

        ArrayList<String> stampSubjectList = new ArrayList<>();

        try {

            //Creating a DocumentBuilder Object
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            //Parsing the XML Document
            Document doc = dBuilder.parse(inputStream);

            //counting Bentley cars
            int count = 0;
            NodeList nList = doc.getElementsByTagName("stamp");
            for (int i = 0; i < nList.getLength(); i++) {
                Element ele = (Element) nList.item(i);
                String stampSubject = ele.getAttribute("subject");
                System.out.println("STAMP Subject >>> " + stampSubject);
                stampSubjectList.add(stampSubject);
//                System.out.println("Icon >>> "+ ele.getAttribute("icon"));
//                System.out.println("Color >>> "+ ele.getAttribute("color"));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stampSubjectList;
    }
}
