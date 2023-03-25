package parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikiHandler extends DefaultHandler{
    private final Map<String, Integer> mapIdToTitle;
    private static final String WIKIS = "mediawiki";
    private static final String PAGE = "page";
    private static final String TITLE = "title";
    private static final String TEXT = "text";
    public static int nbwikipage=0;
    private static Wiki website;
    private StringBuilder elementValue;
    private PrintWriter pw;
    public boolean again=false;

    public WikiHandler(Map<String, Integer> mapIdToTitle){
        super();
        this.mapIdToTitle = mapIdToTitle;
    }

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
        //File file = new File("/Users/harmonysimon-duchatel/M2/maain/maain_moteurrecherche_wikipedia/mywiki.xml");
        File file = new File("mywiki.xml");
        if (!file.exists()) {
            try {
                file.createNewFile();
                pw = new PrintWriter(new BufferedWriter(new FileWriter(file.getAbsoluteFile())));
                pw.print("<corpus>\n");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else{
            //le fichier existe il a donc deja etet paerser
            //on veut donc juste re remplir les structures ...
            again=true;
            website.setAllPageList(new ArrayList<>());
        }
    }
    @Override
    public void endDocument() throws SAXException{
        if(!again){
            pw.print("</corpus>");
            pw.close();
        }else{
            nbwikipage=website.allPageList.size();
        }

        // Serialization of the map for next steps.
        try {
            FileOutputStream fileOutputStream
                    = new FileOutputStream(
                    "IDMappedToTitle.txt");

            ObjectOutputStream objectOutputStream
                    = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(mapIdToTitle);

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
                break;
            case TITLE:
                if (again){
                    website.setPageList(new ArrayList<>());
                    website.getPageList().add(new Wiki.WikiPage());
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
                break;
            case TEXT:
                latestPage().setText(elementValue.toString());
                if(!again){
                    try {
                        writeToFile(website.getPageList(),pw);
                        //remettre a 0 la liste pour afficher que une fois chaque page dans le fichier
                        //et ne pas tout stocker
                        website.setPageList(new ArrayList<>());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    try {
                        rempParam(website.getPageList(), pw);

                        //remettre a 0 la liste pour afficher que une fois chaque page dans le fichier
                        //et ne pas tout stocker
                        website.setPageList(new ArrayList<>());
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
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

    private void rempParam(List<Wiki.WikiPage> list, PrintWriter pw) throws IOException{
        Iterator<Wiki.WikiPage> it = list.iterator();
        list.get(list.size()-1);
        while (it.hasNext()) {
            Wiki.WikiPage n = it.next();
            website.getAllPageList().add(new Wiki.WikiPage());
            latestAllPage().setTitle(n.getTitle());
            latestAllPage().setText(n.getText().toLowerCase());
        }
    }

    private void writeToFile(List<Wiki.WikiPage> list, PrintWriter pw) throws IOException{
        Pattern p;
        Matcher m;
        p = Pattern.compile("aér*|avion");
        // p = Pattern.compile("algo*");
        Iterator<Wiki.WikiPage> it = list.iterator();
        list.get(list.size()-1);

        while (it.hasNext()) {
            Wiki.WikiPage n = it.next();
            m = p.matcher(n.getText().toLowerCase());
            if(m.find()){
                // Removes [[Mot_clé:titre…
                String s = n.getText();
                String t = n.getTitle();
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
                s = s.replaceAll("\\?|!|\\.|,|<|>|:|;|&|('')+|-|%|=|&|\\$|\\€|_|\\+|\\*|\\||`|»|«"," ");
                t = t.replaceAll("\\?|!|\\.|,|<|>|:|;|&|('')+|-|%|=|&|\\$|\\€|_|\\+|\\*|\\||`|»|«"," ");
                // Removes all external links.
                s = s.replaceAll("(<.*>)","");
                //s = s.replaceAll("(\\{+(.*\\n)+}+)|(\\{+.[^\\{]*}+)","");
                //s = s.replaceAll("(\\{+(.*\\n)+}+)","");
                s = s.replaceAll("l’|l'|d’|d'|j'|s'","");
                t = t.replaceAll("l’|l'|d’|d'|j'|s'","");
                s = s.replaceAll(" ","");
                s=s.replaceAll("–"," ");
                s=s.replaceAll("—","");
                s=s.replaceAll("…","");
                s=s.replaceAll("/*","");

                if (s.length()<999){
                    // System.out.println("page trop courte !");
                }else{
                    // Set the ID mapped to the article title if it does not already exist.
                    mapIdToTitle.computeIfAbsent(n.getTitle().toLowerCase(), k -> nbwikipage);
                    n.setId(mapIdToTitle.get(n.getTitle().toLowerCase()));

                    website.getAllPageList().add(new Wiki.WikiPage());
                    latestAllPage().setTitle(t);
                    latestAllPage().setText(s.toLowerCase());
                    // Adds the page link to the global list in order to populate the CLI later.
                    //this.pagesLinks.add(pageLinks);

                    // Write the cleaned page to file.
                    pw.println("<title>" + t + "</title>\n"  + "<text>" + s.toLowerCase() + "</text>");

                    nbwikipage++;
                }
            }
        }
    }
}
