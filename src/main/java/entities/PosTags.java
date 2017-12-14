package entities;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PosTags {
    private static final String[] posTags = {"$", "``", "''", "-LRB-", "-RRB-", ",", "--", ".", ":",
            "CC", "CD", "DT", "EX", "FW", "IN", "JJ", "JJR", "JJS",
            "LS", "MD", "NN", "NNP", "NNPS", "NNS", "PDT", "POS",
            "PRP", "PRP$", "RB", "RBR", "RBS", "RP", "SYM", "TO",
            "UH", "VB", "VBD", "VBG", "VBN", "VBP", "VBZ", "WDT",
            "WP", "WP$", "WRB", "#"};

    private static Map<String, Integer> posTypesMap;

    static {
        Map<String, Integer> aMap = new HashMap();
        aMap.put("$", 0);
        aMap.put("``", 1);
        aMap.put("''", 2);
        aMap.put("-LRB-", 3);
        aMap.put("-RRB-", 4);
        aMap.put(",", 5);
        aMap.put("--", 6);
        aMap.put(".", 7);
        aMap.put(":", 8);
        aMap.put("CC", 9);
        aMap.put("CD", 10);
        aMap.put("DT", 11);
        aMap.put("EX", 12);
        aMap.put("FW", 13);
        aMap.put("IN", 14);
        aMap.put("JJ", 15);
        aMap.put("JJR", 16);
        aMap.put("JJS", 17);
        aMap.put("LS", 18);
        aMap.put("MD", 19);
        aMap.put("NN", 20);
        aMap.put("NNP", 21);
        aMap.put("NNPS", 22);
        aMap.put("NNS", 23);
        aMap.put("PDT", 24);
        aMap.put("POS", 25);
        aMap.put("PRP", 26);
        aMap.put("PRP$", 27);
        aMap.put("RB", 28);
        aMap.put("RBR", 29);
        aMap.put("RBS", 30);
        aMap.put("RP", 31);
        aMap.put("SYM", 32);
        aMap.put("TO", 33);
        aMap.put("UH", 34);
        aMap.put("VB", 35);
        aMap.put("VBD", 36);
        aMap.put("VBG", 37);
        aMap.put("VBN", 38);
        aMap.put("VBP", 39);
        aMap.put("VBZ", 40);
        aMap.put("WDT", 41);
        aMap.put("WP", 42);
        aMap.put("WP$", 43);
        aMap.put("WRB", 44);
        aMap.put("#", 45);
        posTypesMap = Collections.unmodifiableMap(aMap);
    }

    public static String[] getPosTags() {
        return posTags;
    }

    public static Integer getPosTagsLenght() {
        return posTags.length;
    }

    public static Integer getPosIndex(String posTag) {
        return posTypesMap.get(posTag);
    }

    public static String getCSVHeaderString() {
        String posTagString = "";
        for (String tag : posTags) {
            posTagString += "\"" + tag + "\", ";
        }
        posTagString = posTagString.substring(0, posTagString.length() - 2);
        return posTagString;
    }

}
