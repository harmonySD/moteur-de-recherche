//fortement inspirer de https://www.baeldung.com/java-sax-parser
import org.w3c.dom.events.MouseEvent;
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

import javax.swing.plaf.synth.SynthEditorPaneUI;
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
        //sauvegader dans Map<mot,map<title,nb apparition>>
        Map<String,Map<String, Double>> tf= new HashMap<>();
        //pour chaque page de notrer wiki
        for(int i=0; i<wiki.getWebsite().getAllPageList().size(); i++ ){
            WikiPage page= wiki.getWebsite().getAllPageList().get(i);
            //pour chaque mot du dictionnaire 
            Map<String, Double> mot_apparition = new HashMap<>();
            for (Map.Entry<String,Integer> entry : dictionnaire.entrySet()) {
                String word= entry.getKey();
                int count=0; 
                String [] a=page.getText().split(" ");
                for(int j=0; j<a.length; j++){
                    if(a[j].equals(word)){
                        count++;
                    }
                }
                if(count!=0){
                    //mettre resultat dans mini hashmap
                    double miniTF=1+java.lang.Math.log10(count);
                    mot_apparition.put(page.getTitle(), miniTF);
                    tf.put(word, mot_apparition);
                }
            }
        }
        return tf;
    }

    static Map<String,Double> norme_vecteur(Map<String,Map<String,Double>> tf){
        Map<String,Double> nd= new HashMap<>();
        for(Map.Entry<String, Map<String,Double>> entry : tf.entrySet()){
            for(Map.Entry<String, Double> entry2 : entry.getValue().entrySet()){
                if(!nd.containsKey(entry2.getKey())){
                    nd.put(entry2.getKey(), entry2.getValue());
                }else{
                    nd.replace(entry2.getKey(), entry2.getValue()+nd.get(entry2.getKey()));
                }
            }
        }
        return nd;
    }
    
    static Map<String,Map<String, Double>> coeff_TF_normalise(Map<String,Double> norme_vecteur, Map<String,Map<String, Double>> tf){
        Map<String,Map<String, Double>> coef_tf= new HashMap<>();
        for (Map.Entry<String, Map<String,Double>> entry : tf.entrySet()) {
            String word =entry.getKey();
            Map<String, Double> map = new HashMap<>();
            for(Map.Entry<String,Double> entry2 : entry.getValue().entrySet()) {
                Double nd = norme_vecteur.get(entry2.getKey());
                Double calcul=entry2.getValue()/nd;
                map.put(entry2.getKey(), calcul); 
            }
            coef_tf.put(word, map);
        }
        return coef_tf;
        
    }

    static Map<String,Map<String, Double>> supp_page_tf_faible(Map<String,Map<String, Double>> tf, Map<String,Map<String, Double>> coeff_tf_norm, Map<String,Integer> dictionaire){
        // enlever de la list des pages associe au mot 
        Map<String,Map<String, Double>> liste_page_mot=tf;
        for (Map.Entry<String, Map<String,Double>> entry : tf.entrySet()) {
            for(Map.Entry<String,Double> entry2 : entry.getValue().entrySet()) {
                String word_search= entry.getKey();
                Map<String,Double> TF= coeff_tf_norm.get(word_search);
                Double coefTF= TF.get(entry2.getKey());
                Double calcul= idf(dictionaire, entry.getKey())*coefTF;
                if(calcul>0.2){
                    entry.getValue().remove(entry2.getKey());
                    // liste_page_mot.remove(entry.getKey());
                }
            }
        }
        return liste_page_mot;
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

        //map avec pour chaque mot 
        // TF
        //renvoyer map <mot,<title, tf>>
        Map<String,Map<String, Double>> tf = term_freq(Dictionnaire, wikiHandler);
        Map<String,Double> normeVect = norme_vecteur(tf);
        Map<String,Map<String, Double>> tfnorm = coeff_TF_normalise(normeVect, tf);
        Map<String,Map<String, Double>> list_page_mot_tf= supp_page_tf_faible(tf, tfnorm, Dictionnaire);
        System.out.println(list_page_mot_tf.size());
    }

}