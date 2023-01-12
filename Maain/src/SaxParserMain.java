//fortement inspirer de https://www.baeldung.com/java-sax-parser
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.nio.file.Paths;
import java.util.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SaxParserMain {
    private static Map<String, Integer> titleID = new HashMap<>();
    private static Integer pageCount;
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        pageCount = 0;
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();

        WikiHandler wikiHandler = new WikiHandler();
        if(args.length > 0){
            saxParser.parse(Paths.get(args[0]).toAbsolutePath().toString(), wikiHandler);
        }else {
            saxParser.parse("frwiki-latest-pages-articles.xml", wikiHandler);
        }
        System.out.println(wikiHandler.getWebsite().getPageList().size());

        ParserLogger logger = new ParserLogger(pageCount);
        logger.run();
    }

    public static class WikiHandler extends DefaultHandler {
        private static final String WIKIS = "mediawiki";
        private static final String PAGE = "page";
        private static final String TITLE = "title";
        private static final String TEXT = "text";

        private Wiki website;
        private StringBuilder elementValue;

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
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            switch (qName) {
                case WIKIS:
                    website.setPageList(new ArrayList<>());
                    break;
                case PAGE:
                    website.getPageList().add(new WikiPage());
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
                        writeToFile(website.getPageList());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }

        private WikiPage latestPage() {
            List<WikiPage> pageList = website.getPageList();
            int latestPageIndex = pageList.size() - 1;
            return pageList.get(latestPageIndex);
        }

        public Wiki getWebsite() {
            return website;
        }

    }

    static void writeToFile(List<WikiPage> list) throws IOException{
        Pattern p;
        Matcher m;
        p = Pattern.compile("aéro*");
        File file = new File("mywiki.xml");
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        PrintWriter pw = new PrintWriter(fw);
        Iterator<WikiPage> it = list.iterator();

        while (it.hasNext()) {
            SaxParserMain.WikiPage n = it.next();
            m = p.matcher(n.text.toLowerCase());
            if(m.find()){
                titleID.put(n.title, n.id);
                String s = n.text.toLowerCase().replaceAll("\\[(.*:.*)\\]","");
                s = s.replaceAll("^([a-z]|[A-Z])*","");
                s= s.replaceAll("\\{|}","");
                s=s.replaceAll("=+.*=","");
                s=s.replaceAll("\\?|!|\\.|,|:|;|-|_|\\+|\\*|\\||`","");
                s=s.replaceAll("<ref>.*</ref>","");

                pageCount++;
                n.setId(pageCount-1);
                pw.println("<title>"+n.title+"</title>\n"+"<id>"+n.id+"</id>\n"+"<text>"+s.toLowerCase()+"</text>");
            }
        }  
        pw.close();
    }
    public static class Wiki {
        private List<WikiPage> pageList;
        
        public void setPageList(List<WikiPage> pageList) {
            this.pageList = pageList;
        }

        public List<WikiPage> getPageList() {
            return this.pageList;
        }
    }

    public static class WikiPage {
        private String title;
        private String text;
        private int id;

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTitle() {
            return this.title;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getText() {
            return this.text;
        }

        public void setId(int id){
            this.id = id;
        }
    }
}