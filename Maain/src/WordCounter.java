import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


import java.io.*;
import java.util.*;


public class WordCounter extends DefaultHandler {
    //Properties props = new Properties();
    //props.load(IOUtils.readerFromString("StanfordCoreNLP-french.properties"));
    //StanfordCoreNLP corenlp = new StanfordCoreNLP(props);

    private static SortedSet<Map.Entry<String,Integer>> treemap = new TreeSet<>();
    private static final int ARBITRARYNUMBEROFWORDS = 20000;
    private static final HashSet<String> IgnoredWords = new HashSet<>(Arrays.asList("le", "la", "les", "à", "de", "des"
            , "du", "sous", "sur", "dans", "ton", "tu", "je", "il", "nous", "vous", "ils", "elles", "elle", "on", "tous"
            , "tout", "et", "ou", "où", "aux","au","du","que","quel","quelle"));

    public static Map<String,Integer> wordCounter() throws IOException {
        Map<String, Integer> alphabeticallySorted = new TreeMap<>();
        BufferedReader objReader = new BufferedReader(new FileReader("./mywiki.xml"));
        Map<String,Integer> tmpHashMap = new HashMap<>();
        String strCurrentLine;
        while ((strCurrentLine = objReader.readLine()) != null) {
            String correctedStr = strCurrentLine.replaceAll("<.*>.*</.*>","");
            correctedStr = correctedStr.replaceAll("<.*/>", "");
            correctedStr = correctedStr.replaceAll("</.*>","");
            correctedStr = correctedStr.replaceAll("<.*>","");
            correctedStr = correctedStr.replaceAll("/>|<|>","");
            correctedStr = correctedStr.replaceAll("<gallery","");
            correctedStr = correctedStr.replaceAll("style.+","");
            correctedStr = correctedStr.replaceAll("jpg","");
            correctedStr = correctedStr.replaceAll("&|#.*|\".*","");
            correctedStr = correctedStr.replaceAll("'.*","");
            correctedStr = correctedStr.replaceAll("’.*","");
            String[] toExploreStr = correctedStr.toLowerCase(Locale.ROOT).split(" ");
            for (String toCheck : toExploreStr) {
                if (!IgnoredWords.contains(toCheck) && !toCheck.equals("") && !toCheck.equals(" ")
                        && !toCheck.contains("[") && !toCheck.contains("]")) {
                    toCheck = toCheck.trim();
                    if (tmpHashMap.containsKey(toCheck)) {
                        int value = tmpHashMap.get(toCheck);
                        value++;
                        tmpHashMap.replace(toCheck, value);
                    } else {
                        tmpHashMap.put(toCheck, 1);
                    }
                }
            }
        }
        treemap = entriesSortedByValues(tmpHashMap);

        //https://stackoverflow.com/questions/5648336/how-select-first-n-items-in-java-treemap
        int count =0;
        for(Map.Entry<String,Integer> entry:treemap){
            if(count>=ARBITRARYNUMBEROFWORDS){
                break;
            }
            alphabeticallySorted.put(entry.getKey(),entry.getValue());
            count++;
        }
        return alphabeticallySorted;
    }
    static <K,V extends Comparable<? super V>>
    SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
        SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
                new Comparator<Map.Entry<K,V>>() {
                    @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                        int res = e2.getValue().compareTo(e1.getValue());
                        return res != 0 ? res : 1;
                    }
                }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }
}
