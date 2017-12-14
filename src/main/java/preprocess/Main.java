package preprocess;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;


public class Main {
    private static final Logger log = Logger.getLogger(Main.class.getName());
    
    public static void main(String[] args) throws IOException {
//        Options options = new Options();
//        options.addOption("h", "help", false, "show help.");
//        options.addOption("i", "input", true, "input StanfordNLP .XML file path");
//        options.addOption("o", "output", true, "output .CSV file path, default input + suffix");
//        options.addOption("w", "window", true, "sliding window size, default 1 sentence");
//
//        CommandLineParser parser = new DefaultParser();
//        CommandLine cmd;
//        String inFilePath = "";
//        String outFilePath = "";
//        int windowSize = 1;
//        try {
//            cmd = parser.parse(options, args);
//
//            if (cmd.hasOption("h"))
//                help(options);
//
//            if (cmd.hasOption("i")) {
//                inFilePath = cmd.getOptionValue("i");
//            } else {
//                log.log(Level.SEVERE, "input file necessary ");
//                help(options);
//            }
//
//            if (cmd.hasOption("w")) {
//                windowSize = Integer.parseInt(cmd.getOptionValue("w"));
//            }
//
//            if (cmd.hasOption("o")) {
//                outFilePath = cmd.getOptionValue("o");
//            } else {
//                outFilePath = inFilePath + "-vec" + Integer.toString(windowSize) + ".csv";
//            }
//
//        } catch (ParseException e) {
//            log.log(Level.SEVERE, "Failed to parse command line properties", e);
//            help(options);
//        }
//
//        System.out.println("Parsing " + inFilePath + " into " + outFilePath);
//
//        SAXParserFactory factory = SAXParserFactory.newInstance();
//        long startTime = System.nanoTime();
//
//        String sblockFilePath = inFilePath + "-sblock.csv";
//        try {
//            InputStream xmlInput = new FileInputStream(inFilePath);// mini micro anarchist_cookbook shakespear articles
//
//            SAXParser saxParser = factory.newSAXParser();
//            StanfordNLPSaxHandler handler = new StanfordNLPSaxHandler(inFilePath, sblockFilePath);
//            saxParser.parse(xmlInput, handler);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        double[][] normVectors = SlidingWindow.getNormVectors(sblockFilePath);
////        System.out.println("mins: " + Arrays.toString(normVectors[0]));
////        System.out.println("divs: " + Arrays.toString(normVectors[1]));
//
//        ADSentenceBlock sum = SlidingWindow.getSum(sblockFilePath);
////        System.out.println("sum: " + sum.toCSVLine());
//        SlidingWindow.slidingWindow(sblockFilePath,outFilePath, windowSize, sum, normVectors);
////        SlidingWindow.slidingWindow("resources\\test5\\articles-in-translated-sblock.csv","resources\\test5\\articles-in-translated-vector10.csv",
////                "resources\\test5\\articles-in-translated-dist10.csv", 10, sum, normVectors);
////        System.out.println("!" + sum.toCSVLine());
////
////        sum = SlidingWindow.getSum("resources\\test5\\translated-in-articles-sblock.csv");
////        System.out.println("sum: " + sum.toCSVLine());
////        SlidingWindow.slidingWindow("resources\\test5\\translated-in-articles-sblock.csv","resources\\test5\\translated-in-articles-vector1.csv",
////                "resources\\test5\\translated-in-articles-dist1.csv", 1, sum, normVectors);
////        SlidingWindow.slidingWindow("resources\\test5\\translated-in-articles-sblock.csv","resources\\test5\\translated-in-articles-vector10.csv",
////                "resources\\test5\\translated-in-articles-dist10.csv", 10, sum, normVectors);
////        System.out.println("!" + sum.toCSVLine());
//
//
//        System.out.println("Finished. Time elapsed: " + Long.toString((System.nanoTime() - startTime) / 1000000000) + "." +
//                Long.toString(((System.nanoTime() - startTime) / 1000000) % 1000)+ "s");


//        //TODO DEL
//        try {
//            InputStream xmlInput = new FileInputStream(inFilePath);// mini micro anarchist_cookbook shakespear articles
//
//            SAXParser saxParser = factory.newSAXParser();
//            FirstReadSaxHandler firstReadSaxHandler = new FirstReadSaxHandler();
//            saxParser.parse(xmlInput, firstReadSaxHandler);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        long startTime = System.nanoTime();
        File gModel = new File("/Developer/Vector Models/GoogleNews-vectors-negative300.bin.gz");
        Word2Vec vec = WordVectorSerializer.readWord2VecModel(gModel);
        System.out.println(vec.wordsNearest("day", 10));
//        Word2VecModel w2vModel = Word2VecModel.fromTextFile(glove); //-1389934592
//        Searcher searcher = w2vModel.forSearch();                                          //-1389934592
//        ImmutableList<Double> vector = searcher.getRawVector("day");
//        System.out.println(vector);
//        System.out.println(searcher.getMatches("day", 10));
//        Word2VecExamples.demoWord();

        System.out.println("Finished. Time elapsed: " + Long.toString((System.nanoTime() - startTime) / 1000000000) + "." +
                Long.toString(((System.nanoTime() - startTime) / 1000000) % 1000)+ "s");
//        ADSentenceBlock line1 = new ADSentenceBlock(-1, "");
//        line1.loadCSVLine("\"mini.xml\",1,1,43,116,39,0,1,35,0,3,0,1,1, 0, 0, 0, 0,0, 1, 1, 0, 0, 4, 0, 1, 3, 1, 0, 0, 0, 0, 4, 1, 0, 0, 0, 1, 2, 0, 0, 0, 0, 0, 12, 0, 4, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 5, 0, 0, 0, 0, 0");
//        ADSentenceBlock line2 = new ADSentenceBlock(-1, "");
//        line2.loadCSVLine("\"mini.xml\",2,1,32,128,45,0,1,18,2,8,0,0,1, 0, 0, 0, 0,0, 0, 0, 0, 0, 2, 0, 1, 0, 0, 0, 5, 0, 0, 4, 1, 0, 0, 0, 1, 7, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 2, 0, 2, 1, 1, 1, 0, 0, 0, 0");
//        ADSentenceBlock line3 = new ADSentenceBlock(-1, "");
//        line3.loadCSVLine("\"mini.xml\",3,1,19,81,26,0,1,14,2,6,0,0,0, 0, 1, 0, 0,0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 2, 0, 0, 4, 2, 0, 0, 0, 1, 3, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0");
//        ADSentenceBlock sum = new ADSentenceBlock(-1, "");
//        sum.loadCSVLine("\"Sum\",0,58,1052,3957,1285,2,33,691,81,189,6,1,24, 18, 14, 2, 0,0, 5, 5, 7, 7, 60, 0, 58, 7, 30, 33, 131, 3, 1, 106, 79, 4, 2, 0, 16, 156, 16, 0, 40, 2, 1, 52, 0, 40, 0, 0, 5, 0, 24, 0, 61, 3, 23, 20, 30, 22, 2, 0, 0, 1");
//
//        ADSentenceBlock[] lines = {line1, line2, line3};
//        ADVector vector;
//        ADVector complVector;
//        double distance;
//
//        for (ADSentenceBlock line: lines) {
//            vector = new ADVector(line, false);
//            sum.decrease(line);
//            complVector = new ADVector(sum, true);
//            sum.increase(line);
//            distance = vector.manhattanDistanceTo(complVector);
//            System.out.println(vector.toCSVLine() + "," + Double.toString(distance));
//            System.out.println(complVector.toCSVLine() + "," + Double.toString(distance));
//        }

//        ArrayList<Double> maxDists = new ArrayList(3);
//        ArrayList<Boolean> anomalies = new ArrayList(3);
//        double distancesIn[] = {1.0, -5, 7.3, 8, 10.1, 6, 15, 10.1};
//        boolean anomaliesIn[] = {false, false, true, false, false, false, true, true};
//        double minKey;
//        for (int i = 0; i < 3; i++){
//            maxDists.add(i, -100.0);
//            anomalies.add(i, false);
//        }
//        int index;
//        for (int i = 0; i < distancesIn.length; i++) {
//            minKey = Collections.min(maxDists);
//            if (minKey < distancesIn[i]) {
//                index = maxDists.indexOf(minKey);
//                maxDists.set(index, distancesIn[i]);
//                anomalies.set(index, anomaliesIn[i]);
//            }
//            System.out.println(maxDists.toString());
//            System.out.println(anomalies.toString());
//        }
//
//        int i = 15;
//        System.out.println();
//        System.out.println(i / 2.0);
//        System.out.println(i / 2);
    }

//    private static void help(Options options) {
//        HelpFormatter formater = new HelpFormatter();
//
//        formater.printHelp("Main", options);
//        System.exit(0);
//    }
}

//    public static ADWord parseWord(SyllableCounter counter, String word) {
//        ADWord adWord = new ADWord();
//        adWord.setWord(word);
//        adWord.setCharacters(word.length());
//        adWord.setSyllables(counter.count(word));
//        return adWord;
//    }


