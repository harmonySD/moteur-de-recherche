//fortement inspirer de https://www.baeldung.com/java-sax-parser
import org.xml.sax.SAXException;
import parser.WikiHandler;

import java.nio.file.Paths;
import java.util.*;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.io.IOException;

public class SaxParserMain {
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        System.setProperty("jdk.xml.totalEntitySizeLimit", String.valueOf(Integer.MAX_VALUE));
        SAXParser saxParser = factory.newSAXParser();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);

        WikiHandler wikiHandler = new WikiHandler();
        if(args.length > 0){
            saxParser.parse(Paths.get(args[0]).toAbsolutePath().toString(), wikiHandler);
        }else {
            if(args.length==0){
                System.out.println(" Ou est le chemin du fichier a traiter ? 🤨");
            }else{
                saxParser.parse(args[0], wikiHandler);
            }
        }
        Map<String,Integer> map = WordCounter.wordCounter();
        for(Map.Entry<String,Integer> entry: map.entrySet()){
            System.out.println("Cle "+entry.getKey()+" Valeur "+ entry.getValue());
        }

    }

}