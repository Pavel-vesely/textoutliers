package detection;

import entities.ADSentenceBlock;
import entities.ADVector2;
import org.apache.commons.cli.*;
import utils.Config;

import java.io.*;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AutoTester {
    private static final Logger log = Logger.getLogger(AutoTester.class.getName());

    private static String[] METRIC_LABELS= {"all      ", "Guthrie  ", "Guthrie+ " , "word2vec ", "random   "};

    public static void main(String[] args) throws IOException {
        Options options = new Options();
        options.addOption("h", "help", false, "show help.");
        options.addRequiredOption("n", "normal-file", true, "NECESSARY, file path to normal file in sblock.csv format");
        options.addRequiredOption("a", "anomaly-file", true, "NECESSARY, file path to normal anomaly in sblock.csv format");
        options.addOption("w", "window-size", true, "sliding window size, default 10 sentence");
        options.addOption("i", "iterations", true, "number of test runs, default 100");
        options.addOption("s", "normal-size", true, "number of normal sentences in each test, default 500");
        options.addOption("x", "anomaly-size", true, "number of anomalous sentences in each test, must be lower than normal-size, default 20");
        options.addOption("m", "mixed", false, "normalize using mixed file, default false");
        options.addOption("v", "w2v-mode", true, "w2v selection mode: 'sum', 'sum-abs' or 'sum-squared', default 'sum-squared'");
        options.addOption("d", "w2v-idf", false, "use IDF in w2v selection, default false");
        options.addOption("z", "w2v-num", true, "number of selected w2v features, max 300, default 85");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        String normalFilePath = "";
        String anomalyFilePath = "";
        int windowSize = 10;
        int anomalySize = 20;
        int normalSize = 500;
        int iterations = 100;
        boolean normalizeByFile = true;
        String w2vMode = "sum-squared";
        boolean idf = false;
        int w2vNum = 85;
        try {
            cmd = parser.parse(options, args);

            if (cmd.hasOption("h"))
                help(options);

            if (cmd.hasOption("n")) {
                normalFilePath = cmd.getOptionValue("n");
            }

            if (cmd.hasOption("a")) {
                anomalyFilePath = cmd.getOptionValue("a");
            }

            if (cmd.hasOption("w")) {
                windowSize = Integer.parseInt(cmd.getOptionValue("w"));
            }
            if (cmd.hasOption("i")) {
                iterations = Integer.parseInt(cmd.getOptionValue("i"));
            }
            if (cmd.hasOption("normal-size")) {
                normalSize = Integer.parseInt(cmd.getOptionValue("normal-size"));
            }
            if (cmd.hasOption("anomaly-size")) {
                anomalySize = Integer.parseInt(cmd.getOptionValue("anomaly-size"));
            }
            if (cmd.hasOption("m")) {
                normalizeByFile = false;
            }
            if (cmd.hasOption("v")) {
                w2vMode = cmd.getOptionValue("v");
                if (!w2vMode.equals("sum") && !w2vMode.equals("sum-abs") && !w2vMode.equals("sum-squared")) {
                    log.log(Level.SEVERE, "w2v mode must be 'sum', 'sum-abs' or 'sum-squared'");
                    help(options);
                    return;
                }
            }
            if (cmd.hasOption("d")) {
                idf = true;
            }
            if (cmd.hasOption("x")) {
                w2vNum = Integer.parseInt(cmd.getOptionValue("x"));
            }

            if (normalSize <= anomalySize) {
                log.log(Level.SEVERE, "Normal size must be higher than anomaly size");
                return;
            }
        } catch (ParseException e) {
            log.log(Level.SEVERE, "Failed to parse command line properties", e);
            help(options);
            return;
        }


//        File normalFile = new File(".\\resources\\dev3\\mini-articles.txt.xml-sblock.csv");
//        File anomalyFile = new File(".\\resources\\dev3\\micro-anarchist_cookbook.txt.xml-sblock.csv");//c1mini-articles.xml-sblock.csv

        autoTest(windowSize, anomalySize, normalSize, normalFilePath, anomalyFilePath, normalizeByFile, iterations, w2vMode, w2vNum, idf);
    }

        public static void autoTest(int windowSize, int anomalySize, int normalSize, String normalFileName, String anomalyFileName, boolean normalizeByFile, int iterations,
        String w2vMode, int w2vNum, boolean idf) throws IOException {
            File normalFile = new File(normalFileName);
            File anomalyFile = new File(anomalyFileName);//c1mini-articles.xml-sblock.csv
            File allFile = new File(Config.ALL_TEXT_PATH);
            File allSumFile;
            if (w2vMode.equals("sum")) {
                allSumFile = new File(Config.ALL_SUM_PATH);
            } else if (w2vMode.equals("sum-abs")) {
                allSumFile = new File(Config.ALL_SUM_ABS_PATH);
            } else {
                allSumFile = new File(Config.ALL_SUM_SQUARED_PATH);
            }
//            File resultFile = new File(".\\resources\\test2\\results");

//            File testFile = new File(".\\resources\\test2\\a1-sblock.csv");

            BufferedReader normReader = new BufferedReader(new FileReader(normalFile));
            int normLines = 0;
            while (normReader.readLine() != null) normLines++;
            normReader.close();
            BufferedReader anoReader = new BufferedReader(new FileReader(anomalyFile));
            int anoLines = 0;
            while (anoReader.readLine() != null) anoLines++;
            anoReader.close();
            Random rnd = new Random();
            int normOffset;
            int anoOffset;
            int insideOffset;
            BufferedWriter writer;
//            BufferedWriter resultWriter = new BufferedWriter(new FileWriter(resultFile));

            String line;
            int anoStart;
            int anoSize;
            int tempSize;

            int[][] results = new int[5][20];
            for (int[] res : results) {
                Arrays.fill(res, 0);
            }


            ADVector2[] normVectors = SlidingWindow.getNormVectors(allFile.getPath());
            File testFile = null;
            for (int x = 0; x < iterations; x++) {
                testFile = new File(normalFileName + ".test");

                normOffset = rnd.nextInt(normLines - normalSize - 1);
                anoOffset = rnd.nextInt(anoLines - anomalySize- 1);
                insideOffset = rnd.nextInt(normalSize);

                normReader = new BufferedReader(new FileReader(normalFile));
                anoReader = new BufferedReader(new FileReader(anomalyFile));
                writer = new BufferedWriter(new FileWriter(testFile));

                writer.write(normReader.readLine());
                writer.newLine();
                for (int i = -1; i < normOffset; i++) {
                    normReader.readLine();
                }
                for (int i = -1; i < anoOffset; i++) {
                    anoReader.readLine();
                }
                anoStart = 0;
                tempSize = 0;
                anoSize = 0;
                for (int i = 0; i < normalSize; i++) {
                    if (i == insideOffset) {
                        anoStart = tempSize;
                        for (int j = 0; j < anomalySize; j++) {
                            line = anoReader.readLine();
                            writer.write(line);
                            writer.newLine();
                            anoSize += Integer.parseInt(line.split(",")[6].replace(" ", ""));
                        }
                    }
                    line = normReader.readLine();
                    writer.write(line);
                    writer.newLine();
                    tempSize += Integer.parseInt(line.split(",")[6].replace(" ", ""));
                }
                normReader.close();
                anoReader.close();
                writer.close();
    //            System.out.println("Anomaly " + Integer.toString(x) + ": " + Integer.toString(anoStart) + "-" + Integer.toString(anoStart + anoSize));
    //            System.out.println(anoStart + (anoSize / 2));

//                ADVector2[] normVectors = SlidingWindow.getNormVectors(allFile.getPath());
                if (normalizeByFile) {
                    normVectors = SlidingWindow.getNormVectors(testFile.getPath());
                }
                ADSentenceBlock sumBlock = SlidingWindow.getSum(testFile.getPath());

//                int[] selectedW2V = W2VSelect.selectW2VIndices(testFile.getPath(),75, "sum-squared");
                int[] selectedW2V;
                if (idf) {
                    selectedW2V = W2VSelect.selectW2VIndicesIDF(testFile.getPath(), allSumFile.getPath(), w2vNum, w2vMode);
                } else {
                    selectedW2V = W2VSelect.selectW2VIndicesIDF(testFile.getPath(), allSumFile.getPath(), w2vNum, w2vMode);
                }
                ADVector2.setSelectedW2VIndices(selectedW2V);

                AnomalyTreeSet[] sets = SlidingWindow.slidingWindow(testFile.getPath(), testFile.getPath() + ".vec.csv", testFile.getPath() + ".dist.csv", windowSize, sumBlock, normVectors[0], normVectors[1], 20);
                Anomaly anomaly;
                for (int i = 0; i < sets.length; i++) {
                    Iterator iterator;
                    iterator = sets[i].descendingIterator();

                    // displaying the Tree set data
    //                System.out.println("Tree set data in descending order: " + Integer.toString(i));
                    int j = 0;
                    while (iterator.hasNext()) {
                        anomaly = (Anomaly) iterator.next();
    //                    System.out.print(Integer.toString(j) + " ");
    //                    System.out.print(anomaly);
    //                    System.out.println();
                        if ((insideOffset - windowSize / 2 <= anomaly.getStartChar()) && (insideOffset + anomalySize + windowSize / 2) >= anomaly.getEndChar()) {
    //                    if ((((anoStart + anoSize) / 2) > anomaly.getStartChar()) && ((anoStart + anoSize) / 2) < anomaly.getEndChar()) {
                            results[i][j]++;
                            break;
                        }
                        j++;
                    }
                }
                if ((x + 1)%5 == 0) {
                    System.out.println(Integer.toString(x + 1) + " iterations done");
                }
            }
                testFile.delete();

            int j = 0;
            System.out.println("Out of " + Integer.toString(iterations) + " iterations:");
            for (int[] res : results) {
                System.out.print(METRIC_LABELS[j]);
                j++;
                int sum = 0;
                for (int i = 0; i < 20; i++) {
                    sum += res[i];
                    System.out.print(Integer.toString(sum) + ", ");
                }
                System.out.println();
            }
        }

    private static void help(Options options) {
        HelpFormatter formater = new HelpFormatter();

        formater.printHelp("Main", options);
        System.exit(0);
    }

}
