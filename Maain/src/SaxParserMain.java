//fortement inspirer de https://www.baeldung.com/java-sax-parser
import org.xml.sax.SAXException;
import parser.WikiHandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
        //Dictionnaire
        File file = new File("Dictionnaire.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        BufferedWriter dictioBuff = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
        Map<String,Integer> Dictionnaire = WordCounter.wordCounter();
        for(Map.Entry<String,Integer> entry: Dictionnaire.entrySet()){
            dictioBuff.write(entry.getKey() + " " + entry.getValue() +"\n");
            System.out.println("Cle "+entry.getKey()+" Valeur "+ entry.getValue());
        }
        dictioBuff.close();
    }

}