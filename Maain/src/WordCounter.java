import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import parser.Wiki;

import java.io.*;
import java.util.*;


public class WordCounter extends DefaultHandler {
    private static Map<String, Integer> treemap = new TreeMap<>(Collections.reverseOrder());
    private static final int ARBITRARYNUMBEROFWORDS = 20000;

    private static final HashSet<String> IgnoredWords = new HashSet<>(Arrays.asList("le", "la", "les", "à", "de", "des"
            , "du", "sous", "sur", "dans", "ton", "tu", "je", "il", "nous", "vous", "ils", "elles", "elle", "on", "tous", "tout", "et"
            , "ou", "où"));

    public Map<String,Integer> wordCounter() throws IOException {
        Map<String, Integer> alphabeticallySorted = new TreeMap<>();
        BufferedReader objReader = new BufferedReader(new FileReader("./mywiki.xml"));
        String strCurrentLine;
        while ((strCurrentLine = objReader.readLine()) != null) {
            String correctedStr = strCurrentLine.replaceAll("</|\\w*>","");
            String[] toExploreStr = correctedStr.toLowerCase(Locale.ROOT).split(" ");
            for( String toCheck : toExploreStr){
                if (!IgnoredWords.contains(toCheck)){
                    if(treemap.containsKey(toCheck)){
                        int value = treemap.get(toCheck);
                        value++;
                        treemap.replace(toCheck, value);
                    }
                    else {
                        treemap.put(toCheck,1);
                    }
                }
            }

            //https://stackoverflow.com/questions/5648336/how-select-first-n-items-in-java-treemap
            int count =0;
            for(Map.Entry<String,Integer> entry:treemap.entrySet() ){
                if(count>=ARBITRARYNUMBEROFWORDS){
                    break;
                }
                alphabeticallySorted.put(entry.getKey(),entry.getValue());
                count++;
            }
        }
        return alphabeticallySorted;
    }
}
