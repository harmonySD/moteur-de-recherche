//fortement inspirer de https://www.baeldung.com/java-sax-parser
import org.xml.sax.SAXException;

import maths.Matrice;
import maths.Vecteur;
import parser.WikiHandler;
import parser.Wiki.WikiPage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.*;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class SaxParserMain {

    private static Map<String, Integer> mapIdToTitle;
    private static Matrice CLI;

    static Map<String, Double> relation_mot_page(Map<String,Integer> dictionnaire, WikiPage wikipage){
        Map<String, Double> mot_apparition = new HashMap<>();
        String [] a=wikipage.getText().split(" ");
        for(int j=0; j<a.length; j++){
            if(dictionnaire.get(a[j])!= null){
                if (mot_apparition.containsKey(a[j])) {
                    double value = mot_apparition.get(a[j]);
                    value++;
                    mot_apparition.replace(a[j], value);
                } else {
                    mot_apparition.put(a[j], (double) 1);
                }
            }
        }
        return mot_apparition;
    }

    // static void parcours_wiki_to_cli(WikiHandler wiki, Map<String,Integer> dictionnaire){
    //     Matrice matrice;
    //     matrice = new Matrice(wiki.getWebsite().getAllPageList().size());
    //     for(int i=0; i<wiki.getWebsite().getAllPageList().size(); i++ ){
    //         WikiPage page= wiki.getWebsite().getAllPageList().get(i);
    //         Map<String, Double> relation_mp= relation_mot_page(dictionnaire, page);
    //         relation_mp.
    //         List<Integer> page0 = Arrays.asList( 0,3, 5, 8);
    //         matrice.insertPage(page0);

    //     }

    // }


    static double idf(Map<String,Integer> dictionaire, String m){
        double frac=WikiHandler.nbwikipage/dictionaire.get(m);
        return java.lang.Math.log10(frac);
    }

    static Map<String,Map<String, Double>> term_freq(Map<String,Integer> dictionnaire, WikiHandler wiki) throws IOException{
        //parcourir la page une fois compter les occurences de chaque mots pour chaque page
        // et ajouter le resumtats correspondant au tf

        //sauvegader dans Map<mot,map<title,nb apparition>>
        Map<String,Map<String, Double>> tf= new HashMap<>();

        for(int i=0; i<wiki.getWebsite().getAllPageList().size(); i++ ){
            WikiPage page= wiki.getWebsite().getAllPageList().get(i);
            Map<String, Double> mot_apparition = relation_mot_page(dictionnaire, page);
            //la jai dans mot_apparition pour chaque mot qui est contenu dans le dico la frequence d'apparition dasn la page
            for (Map.Entry<String,Integer> entry : dictionnaire.entrySet()) {
                if(mot_apparition.get(entry.getKey())!=null){
                    double miniTF=1+java.lang.Math.log10(mot_apparition.get(entry.getKey()));
                    mot_apparition.put(page.getTitle(), miniTF);
                    tf.put(entry.getKey(), mot_apparition);

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
        // Map<String,Map<String, Double>> liste_page_mot=tf;
        Map<String,Map<String, Double>> m = new HashMap<>();
        m.putAll(tf);
        for (Map.Entry<String, Map<String,Double>> entry : tf.entrySet()) {
            for(Map.Entry<String,Double> entry2 : entry.getValue().entrySet()) {
                String word_search= entry.getKey();
                Map<String,Double> TF= coeff_tf_norm.get(word_search);
                Double coefTF= TF.get(entry2.getKey());
                Double calcul= idf(dictionaire, entry.getKey())*coefTF;
                if(calcul<0.02){
                    HashMap<String,Double> tmp  = new HashMap<>();
                    tmp.put(entry2.getKey(),entry2.getValue());
                    m.put(word_search, tmp);
                    // entry.getValue().remove(entry2.getKey());
                    // liste_page_mot.remove(entry.getKey());
                }
            }
        }
        // return liste_page_mot;
        return m;
    }

    static HashSet<String> requete(String rq){
        HashSet<String> rqcorrect=new HashSet<>();
        HashSet<String> ignore=Dictionnary.IgnoredWords;
        String[] toExploreStr = rq.toLowerCase(Locale.ROOT).split(" ");
        for (String toCheck : toExploreStr) {
            if (!ignore.contains(toCheck) && !toCheck.equals("") && !toCheck.equals(" ")
                    && !toCheck.contains("[") && !toCheck.contains("]")) {
                rqcorrect.add(toCheck);
            }
        }
        return rqcorrect;
    }

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException {
        //wiki
        // File filewiki = new File("mywiki.xml");
        SAXParserFactory factory = SAXParserFactory.newInstance();
        System.setProperty("jdk.xml.totalEntitySizeLimit", String.valueOf(Integer.MAX_VALUE));
        SAXParser saxParser = factory.newSAXParser();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);

        List<List<Integer>> pagesLinks = new ArrayList<>(); // List containing all the pages links to other article (ie :pages).
        File f= new File("pagesLink.txt");
        if(f.exists()){
            try {
                FileInputStream fileInputStream
                        = new FileInputStream(
                        "pagesLink.txt");

                ObjectInputStream objectInputStream
                        = new ObjectInputStream(fileInputStream);

                pagesLinks=(List<List<Integer>>) objectInputStream.readObject();
                System.out.println("TOTO "+pagesLinks.size());

                objectInputStream.close();
                fileInputStream.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        mapIdToTitle = new HashMap<>();
        // Remise en mémoire de mapIdToTitle.
        File map = new File("IDMappedToTitle.txt");
        if(map.exists()) {
            try {
                FileInputStream fileInputStream
                        = new FileInputStream(
                        map);

                ObjectInputStream objectInputStream
                        = new ObjectInputStream(fileInputStream);

                mapIdToTitle = (Map<String, Integer>) objectInputStream.readObject();

                objectInputStream.close();
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        WikiHandler wikiHandler = new WikiHandler(mapIdToTitle);
        if(args.length > 0){
            saxParser.parse(Paths.get(args[0]).toAbsolutePath().toString(), wikiHandler);
        }else {
            if(args.length==0){
                System.out.println(" Ou est le chemin du fichier a traiter ? 🤨");
                // }else{
                //     saxParser.parse(args[0], wikiHandler);
            }
        }

        //Dictionnaire (obligation de refaire le dictionnaire si pagesLinks est vide).
        File file = new File("Dictionnaire.txt");
        Map<String,Integer> Dictionnaire = new TreeMap<>() ;
        if (!file.exists() || pagesLinks.isEmpty()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            BufferedWriter dictioBuff = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
            Dictionnary dico = new Dictionnary(pagesLinks, Collections.unmodifiableMap(mapIdToTitle));
            Dictionnaire = dico.makeDictionnary();
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

        // Matrice CLI.
        if(pagesLinks.isEmpty()){
            throw new RuntimeException(" PAGESLINKS VIDE !!!!! IMPOSSIBLE DE CONTINUER ");
        }
        CLI = new Matrice(wikiHandler.getWebsite().getAllPageList().size());
        for(List<Integer> links : pagesLinks){
            // System.out.println("toto");
            List<Integer> tmp= new ArrayList<>(links);
            CLI.insertPage(tmp);
        }

        // coeef idf a bouger dans le main 
        double coefidf=idf(Dictionnaire, "cinq");
        System.out.println("idf : "+coefidf);

        //map avec pour chaque mot 
        // TF
        //renvoyer map <mot,<title, tf>>

        // Map<String,Map<String, Double>> tf = term_freq(Dictionnaire, wikiHandler);
        // Map<String,Double> normeVect = norme_vecteur(tf);
        // Map<String,Map<String, Double>> tfnorm = coeff_TF_normalise(normeVect, tf);

        // Map<String,Map<String, Double>> list_page_mot_tf= supp_page_tf_faible(tf, tfnorm, Dictionnaire);
        // System.out.println(list_page_mot_tf.size());


        //Matrices

        Matrice matrice;
        matrice = new Matrice(4);
        // List<Integer> page0 = Arrays.asList(35, 8, 50, 12);
        List<Integer> page0 = Arrays.asList( 1, 3, 2);
        matrice.insertPage(page0);

        List<Integer> page1 = Arrays.asList(1,2);
        matrice.insertPage(page1);


        List<Integer> page2 = Arrays.asList();
        matrice.insertPage(page2);

        List<Integer> page3 = Arrays.asList(3);
        matrice.insertPage(page3);


        // Vecteur u = new Vecteur();
        //     u.insertValue(1);
        //     u.insertValue(1);
        //     u.insertValue(1);
        //     u.insertValue(1);

        // Vecteur outVecteur = matrice.multiplyByVector(u);
        // System.out.println(matrice.C); // ca sort dou ca
        // System.out.println(matrice.L); //L ok 
        // System.out.println(matrice.I); //cest C !


        // System.out.println("u "+u.vecteur);
        // System.out.println("out "+outVecteur.vecteur);
        // System.out.println(Arrays.equals(u.toArray(), outVecteur.toArray()));

        //PAGE RANK 🥵 a calculer que si again false
        Vecteur pagerank=new Vecteur();
        if(!wikiHandler.again){
            //rempli pi0
            Vecteur piZero = new Vecteur();
            int  n=wikiHandler.getWebsite().getAllPageList().size();
            float unsurn=1f/n;
            for(int i=0; i<n; i++){
                piZero.insertValue(unsurn);
            }

            System.out.println("norme piO"+piZero.getNorme());
            //calcul pagerank (produit matrice vecteur)
            pagerank=piZero;
            for(int i = 0; i<2; i++){
                pagerank=CLI.multiplyByVector(pagerank);
            }


            //Serialization du vecteur de pagerank 
            try {
                FileOutputStream fileOutputStream
                        = new FileOutputStream(
                        "pageRank.txt");

                ObjectOutputStream objectOutputStream
                        = new ObjectOutputStream(fileOutputStream);

                objectOutputStream.writeObject(pagerank);

                objectOutputStream.close();
                fileOutputStream.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try {
                FileInputStream fileInputStream
                        = new FileInputStream(
                        "pageRank.txt");

                ObjectInputStream objectInputStream
                        = new ObjectInputStream(fileInputStream);

                pagerank =  (Vecteur) objectInputStream.readObject();

                objectInputStream.close();
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("norme pagerank "+pagerank.getNorme());

        // REQUETES
        HashSet<String> r=requete("toto est la bien au chaud");
        System.out.println(r);

        //
    }

}