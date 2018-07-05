package ru.fedrbodr.exchangearbitr.various;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class XmlTest {
	public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		File file = new File("C:\\projects\\exchange-arbitr\\src\\test\\java\\ru\\fedrbodr\\exchangearbitr\\various\\ivm2.xml");
		Document doc = db.parse(file);
		doc.getDocumentElement().normalize();
		System.out.println("Root element " + doc.getDocumentElement().getNodeName());

		NodeList nodeList=doc.getElementsByTagName("p");
		for (int i=0; i<nodeList.getLength(); i++)
		{
			// Get element
			Element element = (Element)nodeList.item(i);
			System.out.println(element.getNodeName());
		}
	}
}
