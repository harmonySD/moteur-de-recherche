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
        website = new Wiki();
        File file = new File("mywikiki.xml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        FileWriter fw;
        try {
            fw = new FileWriter(file.getAbsoluteFile());
            pw = new PrintWriter(new BufferedWriter(fw));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

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


    static void writeToFile(List<Wiki.WikiPage> list, PrintWriter pw) throws IOException{
        Pattern p;
        Matcher m;
        p = Pattern.compile("aéro*");
        Iterator<Wiki.WikiPage> it = list.iterator();

        while (it.hasNext()) {
            Wiki.WikiPage n = it.next();
            m = p.matcher(n.getText().toLowerCase());
            if(m.find()){
                titleID.put(n.getTitle(), n.id);
                String s = n.getText().toLowerCase().replaceAll("\\[(.*:.*)\\]","");
                s = s.replaceAll("^([a-z]|[A-Z])*","");
                s= s.replaceAll("\\{|}","");
                s=s.replaceAll("=+.*=","");
                s=s.replaceAll("\\?|!|\\.|,|:|;|-|_|\\+|\\*|\\||`","");
                s=s.replaceAll("<ref>.*</ref>","");

                pageCount++;
                n.setId(pageCount-1);
                pw.println("<title>"+ n.getTitle() +"</title>\n"+"<id>"+n.id+"</id>\n"+"<text>"+s.toLowerCase()+"</text>");
            }
        }

    }
}
