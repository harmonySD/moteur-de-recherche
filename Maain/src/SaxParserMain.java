//fortement inspirer de https://www.baeldung.com/java-sax-parser
import org.xml.sax.SAXException;
import parser.WikiHandler;
import parser.Wiki.WikiPage;

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

    static double idf(Map<String,Integer> dictionaire, String m){
        double frac=WikiHandler.nbwikipage/dictionaire.get(m);
        return java.lang.Math.log10(frac);
    }

    static Map<String,Map<String, Double>> term_freq(Map<String,Integer> dictionnaire, WikiHandler wiki) throws IOException{
        //mettre title ou id ???
        //sauvegarder Map<title,map<mot, nb apparition>> 
        Map<String,Map<String, Double>> tf= new HashMap<>();
        //pour chaque page de notrer wiki
        for(int i=0; i<wiki.getWebsite().getAllPageList().size(); i++ ){
            WikiPage page= wiki.getWebsite().getAllPageList().get(i);
            //pour chaque mot du dictionnaire 
            Map<String, Double> mot_apparition = new HashMap<>();
            for (Map.Entry<String,Integer> entry : dictionnaire.entrySet()) {
                String word= entry.getKey();
                int count=0; 
                while(page.getText().indexOf(word)!=-1){
                    count++;
                }
                if(count!=0){
                    //mettre resultat dans mini hashmap
                    double miniTF=1+java.lang.Math.log10(count);
                    mot_apparition.put(word, miniTF);
                }
            }
            tf.put(page.getTitle(), mot_apparition);
        }
        return tf;
    }

    static Map<String,Double> norme_vecteur(Map<String,Map<String,Double>> tf, WikiHandler wiki){
        //Pour chaque pages d je calcul Nd 
        //map<title,Nd>
        Map<String,Double> nd= new HashMap<>();
        for(int i=0; i<wiki.getWebsite().getAllPageList().size(); i++ ){
            WikiPage page= wiki.getWebsite().getAllPageList().get(i);
            //recupere la map <mot, nb apparition> qui correspond au titre du if 
            Map<String, Double> tf_m = tf.get(page.getTitle());
            double somme_tf=0;
            for(Map.Entry<String,Double> entry : tf_m.entrySet()){
                somme_tf+=Math.pow(entry.getValue(),2);
            }
            nd.put(page.getTitle(), somme_tf); 
        }
        return nd;
    }
    
    static Map<String,Map<String, Double>> coeff_TF_normalise(Map<String,Double> norme_vecteur, Map<String,Map<String, Double>> tf){
        Map<String,Map<String, Double>> coef_tf= new HashMap<>();
        for (Map.Entry<String, Map<String,Double>> entry : tf.entrySet()) {
            String title =entry.getKey();
            Double nd = norme_vecteur.get(title);
            Map<String, Double> map = new HashMap<>();
            for(Map.Entry<String,Double> entry2 : entry.getValue().entrySet()) {
                Double calcul=entry2.getValue()/nd;
                map.put(entry2.getKey(), calcul); 
            }
            coef_tf.put(title, map);
        }
        return coef_tf;
        
    }
    


    
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

        // coeef idf a bouger dans le main 
        double coefidf=idf(Dictionnaire, "cinq");
        System.out.println("idf : "+coefidf);

        // term_freq(Dictionnaire, wikiHandler);
    }

}