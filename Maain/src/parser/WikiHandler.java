package parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.plaf.synth.SynthEditorPaneUI;

public class WikiHandler extends DefaultHandler{
    private static final Map<String, Integer> titleID = new HashMap<>();
    private static final String WIKIS = "mediawiki";
    private static final String PAGE = "page";
    private static final String TITLE = "title";
    private static final String TEXT = "text";

    public static int nbId=0;
    public static int nbwikipage=0;
    private static Wiki website;
    private StringBuilder elementValue;
    private PrintWriter pw;
    private boolean again=false;

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (elementValue == null) {
            elementValue = new StringBuilder();
        } else {
            elementValue.append(ch, start, length);
        }
    }

    @Override
    public void startDocument() throws SAXException {
        website = new Wiki();
        File file = new File("/Users/harmonysimon-duchatel/M2/maain/maain_moteurrecherche_wikipedia/mywiki.xml");
        if (!file.exists()) {
            try {
                file.createNewFile();
                pw = new PrintWriter(new BufferedWriter(new FileWriter(file.getAbsoluteFile())));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else{
            //le fichier existe il a donc deja etet paerser
            //on veut donc juste re remplir les structures ...
             again=true;
            //  website.setPageList(new ArrayList<>());
            // website.setAllPageList(new ArrayList<>());
            // website.setPageList(new ArrayList<>());
            System.out.println("coucou");
        }

        // try {
        //     pw = new PrintWriter(new BufferedWriter(new FileWriter(file.getAbsoluteFile())));
        // } catch (IOException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
    }
    @Override
    public void endDocument() throws SAXException{
        System.out.println("il me reste "+ nbwikipage+ " pages");
        System.out.println(website.allPageList.size());
        // for(int i=0; i<website.allPageList.size();i++){
        //     System.out.println(website.allPageList.get(i).title);
        // }
        if(again==false){
            pw.close();
        }
        // Serialization of the map for next steps.
        try {
            FileOutputStream fileOutputStream
                    = new FileOutputStream(
                    "IDMappedToTitle.txt");

            ObjectOutputStream objectOutputStream
                    = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(titleID);

            objectOutputStream.close();
            fileOutputStream.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        switch (qName) {
            case WIKIS:
                website.setPageList(new ArrayList<>());
                website.setAllPageList(new ArrayList<>());
                break;
            case PAGE:
                website.getPageList().add(new Wiki.WikiPage());
                // website.getAllPageList().add(new Wiki.WikiPage());
                break;
            case TITLE:
                if (again==true){
                    website.setPageList(new ArrayList<>());
                    website.setAllPageList(new ArrayList<>());
                    website.getPageList().add(new Wiki.WikiPage());
                    // website.getAllPageList().add(new Wiki.WikiPage());
                }
                elementValue = new StringBuilder();
                break;
            case TEXT:
                elementValue = new StringBuilder();
                break;
            default: 
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (qName) {
            case TITLE:
                latestPage().setTitle(elementValue.toString());
                // latestAllPage().setTitle(elementValue.toString());
                break;
            case TEXT:
                latestPage().setText(elementValue.toString());
                // latestPage().setText(elementValue.toString());
                if(again==false){
                    try {
                        writeToFile(website.getPageList(),pw);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    try {
                        rempParam(website.getPageList(), pw);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            

                //remettre a 0 la liste pour afficher que une fois chaque page dans le fichier
                //et ne pas tout stocker
                website.setPageList(new ArrayList<>());
                break;
            
        }
    }

    private static Wiki.WikiPage latestPage() {
        List<Wiki.WikiPage> pageList = website.getPageList();
        int latestPageIndex = pageList.size() - 1;
        return pageList.get(latestPageIndex);
    }
    private static Wiki.WikiPage latestAllPage() {
        List<Wiki.WikiPage> allpageList = website.getAllPageList();
        int latestallPageIndex = allpageList.size() - 1;
        return allpageList.get(latestallPageIndex);
    }

    public Wiki getWebsite() {
        return website;
    }

    private static void rempParam(List<Wiki.WikiPage> list, PrintWriter pw) throws IOException{
        Iterator<Wiki.WikiPage> it = list.iterator();
        list.get(list.size()-1);
        while (it.hasNext()) {
            Wiki.WikiPage n = it.next();
            // System.out.println(n.getTitle());
            // System.out.println(n.getText());
            website.getAllPageList().add(new Wiki.WikiPage());
            latestAllPage().setTitle(n.getTitle());
            latestAllPage().setText(n.getText().toLowerCase());
        }
    }

    private static void writeToFile(List<Wiki.WikiPage> list, PrintWriter pw) throws IOException{
        Pattern p;
        Matcher m;
        p = Pattern.compile("aér*|avion");
        Iterator<Wiki.WikiPage> it = list.iterator();
        list.get(list.size()-1);
        while (it.hasNext()) {
            Wiki.WikiPage n = it.next();
            // Wiki.WikiPage n =list.get(list.size()-1);
            m = p.matcher(n.getText().toLowerCase());
            if(m.find()){
                nbId++;
                nbwikipage++;
                // Set the ID mapped to the article title if it does not already exist.
                titleID.computeIfAbsent(n.getTitle(), k -> nbId);
                n.setId(titleID.get(n.getTitle()));

                // Removes [[Mot_clé:titre…
                String s = n.getText();
                s=s.replaceAll("(http|ftp|https):\\/\\/([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:\\/~+#-]*[\\w@?^=%&\\/~+#-])\n","");
                s= s.replaceAll("\\{\\{.+| \\|.*}*","");
                s = s.replaceAll("\\[\\[(\\[0-9]*)]]","");
                s = s.replaceAll("[0-9]*","");
                s = s.replaceAll("(\\{+)", "<ref>");
                s = s.replaceAll("(\\}+)", "</ref>");
                s = s.replaceAll("\\[(.*:.*)\\]","");
                s = s.replaceAll("^([a-z]|[A-Z])*","");
                // Removes [[666...]].
                s = s.replaceAll("\\[\\[(\\d*)\\]]","");
                //s = s.replaceAll("\\{|}","");
                //Removes all (  ).
                s = s.replaceAll("\\(|\\)","");
                s = s.replaceAll("=+.*=","");
                // Removes all punctuation signs.
                s = s.replaceAll("\\?|!|\\.|,|:|;|('')+|-|%|=|\\$|\\€|_|\\+|\\*|\\||`|»|«"," ");
                // Removes all external links.
                s = s.replaceAll("(<.*>)","");
                //s = s.replaceAll("(\\{+(.*\\n)+}+)|(\\{+.[^\\{]*}+)","");
                //s = s.replaceAll("(\\{+(.*\\n)+}+)","");
                s = s.replaceAll("l’|l'|d’|d'|j'|s'","");
                s = s.replaceAll(" ","");
                s=s.replaceAll("–"," ");
                s=s.replaceAll("—","");
                s=s.replaceAll("…","");
                s=s.replaceAll("/*","");
                // Search for [[Article]] and replaces it with [[id]].
                Pattern pattern = Pattern.compile("\\[\\[[[A-Za-zÀ-ÖØ-öø-ÿ]+| ]*]]");
                Matcher matcher = pattern.matcher(s);
                StringBuilder sb = new StringBuilder();
                while (matcher.find()) {
                    String title = matcher.group(0).substring(2, matcher.group(0).length()-2);
                    if(!titleID.containsKey(title)) {
                        nbId++;
                        titleID.put(title, nbId);
                    }
                    matcher.appendReplacement(sb, "[[" + titleID.get(title) + "]]");
                }
                matcher.appendTail(sb);
                if(!sb.toString().equals("")) {
                    website.getAllPageList().add(new Wiki.WikiPage());
                    latestAllPage().setTitle(n.getTitle());
                    latestAllPage().setText(sb.toString().toLowerCase());
                    pw.println("<title>" + n.getTitle() + "</title>\n"  + "<text>" + sb.toString().toLowerCase() + "</text>");
                }
                if (s.length()<999){
                    // System.out.println("page trop courte !");
                }else{
                    if(!s.isBlank()) {
                        pw.println("<title>" + n.getTitle() + "</title>\n"  + "<text>" + s.toLowerCase() + "</text>");
                    }
                }
            }
        }
    }
}
