package parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikiHandler extends DefaultHandler{
    private static Map<String, Integer> titleID = new HashMap<>();
    private static Integer pageCount;
    private static final String WIKIS = "mediawiki";
    private static final String PAGE = "page";
    private static final String TITLE = "title";
    private static final String TEXT = "text";

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
        pageCount = 0;
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

        // Should log the number of pages written. Does not work currently.
        ParserLogger logger = new ParserLogger(pageCount);
        logger.run();
    }
    @Override
    public void endDocument() throws SAXException{
        pw.close();
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
        p = Pattern.compile("aéro*");
        Iterator<Wiki.WikiPage> it = list.iterator();

        while (it.hasNext()) {
            Wiki.WikiPage n = it.next();
            m = p.matcher(n.getText().toLowerCase());
            if(m.find()){
                titleID.put(n.getTitle(), n.id);
                // Removes [[Mot_clé:titre…
                String s = n.getText().toLowerCase();
                s = s.replaceAll("\\[(.*:.*)\\]","");
                s = s.replaceAll("^([a-z]|[A-Z])*","");
                // Removes [[666...]].
                s = s.replaceAll("\\[\\[(\\d*)\\]]","");
                //s = s.replaceAll("\\{|}","");
                s = s.replaceAll("\\(|\\)","");
                s = s.replaceAll("=+.*=","");
                // Removes all punctuation signs.
                s = s.replaceAll("\\?|!|\\.|,|:|;|'|-|%|=|\\$|\\€|_|\\+|\\*|\\||`","");
                // Removes all external links.
                s = s.replaceAll("(<.*?>)","");

                pageCount++;
                n.setId(pageCount-1);
                pw.println("<title>"+ n.getTitle() +"</title>\n"+"<id>"+n.id+"</id>\n"+"<text>"+s.toLowerCase()+"</text>");
            }
        }

    }
}