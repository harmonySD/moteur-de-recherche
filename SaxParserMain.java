//fortement inspirer de https://www.baeldung.com/java-sax-parser
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import java.util.Iterator;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SaxParserMain {
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();

        WikiHandler wikiHandler = new WikiHandler();
        saxParser.parse("frwiki-latest-pages-articles.xml", wikiHandler);
        System.out.println(wikiHandler.getWebsite().getPageList().size());
        // File file = new File("mywiki.xml");
        // FileWriter fw = new FileWriter(file.getAbsoluteFile());
        // BufferedWriter bw = new BufferedWriter(fw);
        // if (!file.exists()) {
        //     try {
        //         file.createNewFile();
        //     } catch (IOException e) {
        //         e.printStackTrace();
        //     }
        // }
        // for(int i=0; i<wikiHandler.getWebsite().getPageList().size();i++){
        //     WikiPage b=wikiHandler.getWebsite().getPageList().get(i);
        //     bw.write("<title>" + b.title + "</title> \n");
        //     bw.write("<text>" + b.text + "</text> \n");
        // }
    }

    public static class WikiHandler extends DefaultHandler {
        private static final String WIKIS = "mediawiki";
        private static final String PAGE = "page";
        private static final String TITLE = "title";
        private static final String TEXT = "text";
        int count=0;

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
                    latestPage().settext(elementValue.toString());
                    try {
                        writeToFile(website.getPageList());
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
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

        // Collections.sort(list);
        File file = new File("mywiki.txt");
    
    
        if (!file.exists()) {
            file.createNewFile();
        }
    
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        PrintWriter pw = new PrintWriter(fw);
    
        Iterator<WikiPage> it = list.iterator();
        while (it.hasNext()) {
            pw.println(it.next().title);
        }
        pw.println("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz");   
        pw.close();
        // System.out.println(++counterz + "Done");
    }

    private static String getSuffix() {
        return null;
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

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTitle() {
            return this.title;
        }

        public void settext(String text) {
            this.text = text;
        }

        public String gettext() {
            return this.text;
        }
    }
}