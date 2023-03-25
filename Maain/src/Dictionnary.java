import org.xml.sax.helpers.DefaultHandler;


import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Dictionnary extends DefaultHandler {
    private final List<List<Integer>> pagesLinks;
    private final Map<String, Integer> readOnlyMapIdToTitle;

    //Properties props = new Properties();
    //props.load(IOUtils.readerFromString("StanfordCoreNLP-french.properties"));
    //StanfordCoreNLP corenlp = new StanfordCoreNLP(props);

    private static SortedSet<Map.Entry<String,Integer>> treemap = new TreeSet<>();
    private static final int ARBITRARYNUMBEROFWORDS = 20000;
    public static final HashSet<String> IgnoredWords = new HashSet<>(Arrays.asList("le", "la", "les", "à", "de", "des"
            , "du", "sous", "sur", "dans", "ton", "tu", "je", "il", "nous", "vous", "ils", "elles", "elle", "on", "tous"
            , "tout", "et", "ou", "où", "aux","au","du","que","quel","quelle"));

    public Dictionnary(List<List<Integer>> pagesLinks, Map<String, Integer> ReadOnlyMapIdToTitle) {
        this.pagesLinks = pagesLinks;
        this.readOnlyMapIdToTitle = ReadOnlyMapIdToTitle;
    }

    public Map<String,Integer> makeDictionnary() throws IOException {
        Map<String, Integer> alphabeticallySorted = new TreeMap<>();
        BufferedReader objReader = new BufferedReader(new FileReader("./mywiki.xml"));
        Map<String,Integer> tmpHashMap = new HashMap<>();
        String strCurrentLine;
        List<Integer> pageLinks = new ArrayList<>(); // List of all the links to other pages.

        while ((strCurrentLine = objReader.readLine()) != null) {
            String correctedStr = strCurrentLine.replaceAll("<.*>.*</.*>","");
            correctedStr = correctedStr.replaceAll("<.*/>", "");
            correctedStr = correctedStr.replaceAll("</.*>","");
            correctedStr = correctedStr.replaceAll("<.*>","");
            correctedStr = correctedStr.replaceAll("/>|<|>","");
            correctedStr = correctedStr.replaceAll("<gallery","");
            correctedStr = correctedStr.replaceAll("style.+","");
            correctedStr = correctedStr.replaceAll("jpg","");
            correctedStr = correctedStr.replaceAll("&|#.*|\".*|@.*","");
            correctedStr = correctedStr.replaceAll("'.*","");
            correctedStr = correctedStr.replaceAll("’.*","");

            // Search for [[Article]] and replaces it with [[id]].
            Pattern pattern = Pattern.compile("\\[\\[[[A-Za-zÀ-ÖØ-öø-ÿ]+| ]*]]");
            Matcher matcher = pattern.matcher(strCurrentLine);
            StringBuilder sb = new StringBuilder();
            while (matcher.find()) {
                String title = matcher.group(0).substring(2, matcher.group(0).length()-2).toLowerCase();
                Integer articleId = readOnlyMapIdToTitle.get(title);
                if(articleId != null) {
                    pageLinks.add(articleId);
                }
                matcher.appendReplacement(sb, title);
            }
            matcher.appendTail(sb);

            if(strCurrentLine.contains("</text>")){
                //System.out.println("DICO FIN DE PAGE");
                pagesLinks.add(pageLinks);
                pageLinks = new ArrayList<>();
            }

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

        // Serialization of the listset for next steps.
        try {
            FileOutputStream fileOutputStream
                    = new FileOutputStream(
                    "pagesLink.txt");

            ObjectOutputStream objectOutputStream
                    = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(pagesLinks);
            System.out.println(pagesLinks.size());

            objectOutputStream.close();
            fileOutputStream.close();
        }
        catch (IOException e) {
            e.printStackTrace();
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
