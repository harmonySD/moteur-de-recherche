//fortement inspirer de https://www.baeldung.com/java-sax-parser
import org.xml.sax.SAXException;
import parser.WikiHandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.security.KeyStore.Entry;
import java.util.*;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.io.IOException;


public class SaxParserMain {

    // static double idf(Map<String,Integer> dictionaire, String m){
    //     double frac=WikiHandler.nbId/dictionaire.get(m);
    //     return java.lang.Math.log10(frac);
    // }

    // static void term_freq(Map<String,Integer> dictionnaire, WikiHandler wiki) throws IOException{
    //     //lire page par page 
    //     Map<Integer,Map<String, Integer>> tf= new HashMap<>();
    //     tf.put(1, new HashMap<>());
        
    //     BufferedReader objReader = new BufferedReader(new FileReader("../mywiki.xml"));
    //     String strCurrentLine;
    //     //pour chaque page de notrer wiki
    //     for(int i=0; i<wiki.getWebsite().pageList.size(); i++ ){
    //         //pour chaque mot du dictionnaire 
    //         for (Map.Entry<String,Integer> entry : dictionnaire.entrySet()) {
    //             String contenu_page =wiki.getWebsite().pageList.get(i).getText();
    //             contenu_page.
    //             entry.getValue();

    //         }
    //     }
    //         String page =wiki.getWebsite().pageList.get(i).getText();
        
    //     while ((strCurrentLine = objReader.readLine()) != null) {
            
    //     }

    // }
    
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        //wiki
        // File filewiki = new File("mywiki.xml");

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
            // }else{
            //     saxParser.parse(args[0], wikiHandler);
            }
        }

        //Dictionnaire
        File file = new File("Dictionnaire.txt");
        Map<String,Integer> Dictionnaire = new TreeMap<>() ;
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            BufferedWriter dictioBuff = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
            Dictionnaire = Dictionnary.makeDictionnary();
            for(Map.Entry<String,Integer> entry: Dictionnaire.entrySet()){
                dictioBuff.write(entry.getKey() + " " + entry.getValue() +"\n");
            }
            dictioBuff.close();
        }
        else{
            Scanner sc = new Scanner(file);
            while(sc.hasNextLine()){
                String line = sc.nextLine();
                String[] keyValue  = line.split(" ");
                Dictionnaire.put(keyValue[0],Integer.parseInt(keyValue[1]));
            }
        }

        //coeef idf a bouger dans le main 
        // double coefidf=idf(Dictionnaire, "cinq");
        // System.out.println("idf : "+coefidf);
    }

}