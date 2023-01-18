//fortement inspirer de https://www.baeldung.com/java-sax-parser
import org.xml.sax.SAXException;
import parser.ParserLogger;
import parser.WikiHandler;

import java.nio.file.Paths;
import java.util.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.io.IOException;

public class SaxParserMain {
    private static Integer pageCount;
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        pageCount = 0;
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();

        WikiHandler wikiHandler = new WikiHandler();
        if(args.length > 0){
            saxParser.parse(Paths.get(args[0]).toAbsolutePath().toString(), wikiHandler);
        }else {
            saxParser.parse("../../frwiki-latest-pages-articles.xml", wikiHandler);
        }
        System.out.println(wikiHandler.getWebsite().getPageList().size());

        //TODO Fix logger.
        ParserLogger logger = new ParserLogger(pageCount);
        logger.run();
    }
}