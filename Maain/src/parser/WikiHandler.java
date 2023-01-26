package parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikiHandler extends DefaultHandler{
    private static final Map<String, Integer> titleID = new HashMap<>();
    private static final String WIKIS = "mediawiki";
    private static final String PAGE = "page";
    private static final String TITLE = "title";
    private static final String TEXT = "text";

    private static int nbId=0;
    private Wiki website;
    private StringBuilder elementValue;
    private PrintWriter pw;

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
        File file = new File("mywiki.xml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        try {
            pw = new PrintWriter(new BufferedWriter(new FileWriter(file.getAbsoluteFile())));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Override
    public void endDocument() throws SAXException{
        System.out.println("il me reste "+ nbId+ " pages");
        pw.close();
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
                break;
            case PAGE:
                website.getPageList().add(new Wiki.WikiPage());
                break;
            case TITLE:
                elementValue = new StringBuilder();
                break;
            case TEXT:
                elementValue = new StringBuilder();
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
                try {
                    writeToFile(website.getPageList(),pw);
                    //remettre a 0 la liste pour afficher que une fois chaque poage dans le fichier
                    //et ne pas tout stocker
                    website.setPageList(new ArrayList<>());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private Wiki.WikiPage latestPage() {
        List<Wiki.WikiPage> pageList = website.getPageList();
        int latestPageIndex = pageList.size() - 1;
        return pageList.get(latestPageIndex);
    }

    public Wiki getWebsite() {
        return website;
    }


    private static void writeToFile(List<Wiki.WikiPage> list, PrintWriter pw) throws IOException{
        Pattern p;
        Matcher m;
        p = Pattern.compile("aér*|avion");
        Iterator<Wiki.WikiPage> it = list.iterator();
        while (it.hasNext()) {
            Wiki.WikiPage n = it.next();
            m = p.matcher(n.getText().toLowerCase());
            if(m.find()){
                nbId++;
                // Set the ID mapped to the article title if it does not already exist.
                titleID.computeIfAbsent(n.getTitle(), k -> nbId);
                n.setId(titleID.get(n.getTitle()));

                // Removes [[Mot_clé:titre…
                String s = n.getText().toLowerCase();
                s= s.replaceAll("\\{\\{.+| \\|.*}*","");
                s = s.replaceAll("\\[\\[(\\[0-9]*)\\]]","").trim();
                s = s.replaceAll("[0-9]*","").trim();
                s = s.replaceAll("(\\{+)", "<ref>");
                s = s.replaceAll("(\\}+)", "</ref>");
                s = s.replaceAll("\\[(.*:.*)\\]","").trim();
                s = s.replaceAll("^([a-z]|[A-Z])*","").trim();
                // Removes [[666...]].
                //s = s.replaceAll("\\{|}","");
                //Removes all (  ).
                s = s.replaceAll("\\(|\\)","").trim();
                s = s.replaceAll("=+.*=","").trim();
                // Removes all punctuation signs.
                s = s.replaceAll("\\?|!|\\.|,|:|;|'|-|%|=|\\$|\\€|_|\\+|\\*|\\||`|»|«","").trim();
                // Removes all external links.
                s = s.replaceAll("(<.*?>)","").trim();
                //s = s.replaceAll("(\\{+(.*\\n)+}+)|(\\{+.[^\\{]*}+)","");
                //s = s.replaceAll("(\\{+(.*\\n)+}+)","");

                // Search for [[Article]] and replaces it with [[id]].
                Pattern pattern = Pattern.compile("\\[\\[[\\w+| ]*]]");
                Matcher matcher = pattern.matcher(s);
                StringBuilder sb = new StringBuilder();
                StringBuilder replacement = new StringBuilder("[[");
                while (matcher.find()) {
                    if(!titleID.containsKey(matcher.group(1))) {
                        nbId++;
                        titleID.put(matcher.group(1), nbId);
                    }
                    replacement.append(titleID.get(matcher.group(1)));
                    replacement.append("]]");
                    matcher.appendReplacement(sb, replacement.toString());
                    System.out.println(matcher.group(1));
                }
                matcher.appendTail(sb);
                pw.println("<title>"+ n.getTitle() +"</title>\n"+"<id>"+n.id+"</id>\n"+"<text>"+sb.toString().toLowerCase()+"</text>");
                
                if (s.length()<999){
                    // System.out.println("page trop courte !");
                }else{
                    pw.println("<title>"+ n.getTitle() +"</title>\n"+"<id>"+n.id+"</id>\n"+"<text>"+s.toLowerCase()+"</text>");
                }
            }
        }
    }
}
