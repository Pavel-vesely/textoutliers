package preprocess;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import utils.RankMap;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class FirstReadSaxHandler extends DefaultHandler {
    private StringBuilder content;
    private Map<String, Integer> dependencyMap;
    private String depType;
    private final int DEP_AMOUNT = 30;
    private int allDependencies = 0;

    public FirstReadSaxHandler() throws IOException {
        content = new StringBuilder();
        dependencyMap = new TreeMap<String, Integer>();
    }

    public void startElement(String uri, String localName, String qName,
                             Attributes atts) throws SAXException {
        content = new StringBuilder();
        if (qName.equalsIgnoreCase("dep")) {
            allDependencies++;
            depType = atts.getValue("type");
            if (dependencyMap.containsKey(depType)) {
                dependencyMap.replace(depType, dependencyMap.get(depType) + 1);
            } else {
                dependencyMap.put(depType, 1);
            }
        }
    }

    public void endElement(String uri, String localName, String qName)
            throws SAXException {
//        if (qName.equalsIgnoreCase("word")) {
//            word = content.toString();
//            token.setWord(word);
//            token.setCharacters(word.length());
//            token.setSyllables(counter.count(word));
////            System.out.println("word: " + word);
//        } else if (qName.equalsIgnoreCase("lemma")) {
//            token.setLemma(content.toString());
//        } else if (qName.equalsIgnoreCase("pos")) {
//            token.setPOS(content.toString());
//
////            System.out.println("pos: " + content.toString());
//
//        } else if (qName.equalsIgnoreCase("token")) {
//            tokens.add(token);
//
//        } else if (qName.equalsIgnoreCase("sentence")) {
//            if (passive) {
//                inSentence.setPassive(true);
//            }
//            inSentence.setTokens(tokens);
//            adSentenceBlock = new ADSentenceBlock(inSentence, header);
//            try {
//                outBw.write(adSentenceBlock.toCSVLine());
//                outBw.newLine();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
////            System.out.println("Sentence: " + Integer.toString(inSentence.getId()));
//            //sumADSB.increase(adSentenceBlock);
        }

    public void characters(char[] ch, int start, int length)
            throws SAXException {
        content.append(ch, start, length);
    }

    public void endDocument() throws SAXException {
        // you can do something here for example send
        // the Channel object somewhere or whatever.
//        System.out.println(sumADSB.toCSVLine());
        System.out.println(dependencyMap.toString());

        System.out.println(RankMap.getRankMap(dependencyMap, null).toString());

        System.out.println(RankMap.getRankMap(dependencyMap, 10).toString());
    }
}
