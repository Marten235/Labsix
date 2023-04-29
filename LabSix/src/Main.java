import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
// Checked exception for invalid file extension
class NotValidAutosarFileException extends Exception {
    public NotValidAutosarFileException(String message) {
        super(message);
    }
}

// Unchecked exception for empty file
class EmptyAutosarFileException extends RuntimeException {
    public EmptyAutosarFileException(String message) {
        super(message);
    }
}
public class Main {
    public static void main(String[] args) {
        try {
            //creating a constructor of file class and parsing an XML file
            File file = new File("Marten.arxml");
            //an instance of factory that gives a document builder
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            //an instance of builder to parse the specified xml file
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("CONTAINER");
            // nodeList is not iterable, so we are using for loop
            List<Element> elements = new ArrayList<>();
            for (int itr = 0; itr < nodeList.getLength(); itr++) {
                Node node = nodeList.item(itr);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) node;
                    elements.add(eElement);
                }
            }
            // Sort the elements by the value of the SHORT-NAME element
            Collections.sort(elements, new Comparator<Element>() {
                @Override
                public int compare(Element e1, Element e2) {
                    String s1 = e1.getElementsByTagName("SHORT-NAME").item(0).getTextContent();
                    String s2 = e2.getElementsByTagName("SHORT-NAME").item(0).getTextContent();
                    return s1.compareTo(s2);
                }
            });
            // Create a new document with the sorted elements
            Document outputDoc = db.newDocument();
            Element root = outputDoc.createElement(doc.getDocumentElement().getNodeName());
            outputDoc.appendChild(root);
            for (Element eElement : elements) {
                Element element = outputDoc.createElement(eElement.getNodeName());
                element.setAttribute("UUID", eElement.getAttribute("UUID"));
                Element shortName = outputDoc.createElement("SHORT-NAME");
                shortName.appendChild(outputDoc.createTextNode(eElement.getElementsByTagName("SHORT-NAME").item(0).getTextContent()));
                element.appendChild(shortName);
                Element longName = outputDoc.createElement("LONG-NAME");
                longName.appendChild(outputDoc.createTextNode(eElement.getElementsByTagName("LONG-NAME").item(0).getTextContent()));
                element.appendChild(longName);
                root.appendChild(element);
            }
            // Write the output document to a file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(outputDoc);
            StreamResult result = new StreamResult(new File("Marten_mod.arxml"));
            transformer.transform(source, result);

        if (!file.getName().endsWith(".arxml")) {
            throw new NotValidAutosarFileException("File must have .arxml extension");
        }

        // Check if file is empty
        if (file.length() == 0) {
            throw new EmptyAutosarFileException("File is empty");
        }
        // Continue with existing code...
    } catch (NotValidAutosarFileException e) {
        e.printStackTrace();
        // Handle the exception appropriately
    } catch (EmptyAutosarFileException e) {
        e.printStackTrace();
        // Handle the exception appropriately
    }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
