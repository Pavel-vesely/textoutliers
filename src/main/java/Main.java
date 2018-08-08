
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import detection.AnomalyTreeSet;
import detection.SlidingWindow;
import org.bytedeco.javacv.FrameFilter;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.*;
import org.xml.sax.SAXException;
import preprocess.Preprocess;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class Main {
//    private static final Logger log = Logger.getLogger(Main.class.getName());
    
    public static void main(String[] args) {

        Options options = new Options();
        options.addOption("h", "help", false, "show help.");
        options.addOption("i", "input", true, "input StanfordNLP .XML file path");
        options.addOption("m", "mode", true, "mode of program. preprocess (p)/search(s)/preprocess+search(a), default all");
        options.addOption("w", "window", true, "sliding window size, default 10 sentences");
        options.addOption("f", "folder", true, "input is a folder, all files within to be processed");
//        options.addOption("b", "buffer-size", true, "number of ");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        String inFilePath = "";
        String folderPath = "";
        String mode = "a";
        int windowSize = 10;
        try {
            cmd = parser.parse(options, args);

            if (cmd.hasOption("h"))
                help(options);

            if (cmd.hasOption("i")) {
                inFilePath = cmd.getOptionValue("i");
            }
            if (cmd.hasOption("f")) {
                folderPath = cmd.getOptionValue("f");
            }
            if (!cmd.hasOption("i") && !cmd.hasOption("f")) {
                System.out.println("input file or folder path necessary ");
                help(options);
                return;
            }
            if (cmd.hasOption("i") && cmd.hasOption("f")) {
                System.out.println("input file and folder path cannot be set simultaneously ");
                help(options);
                return;
            }

            if (cmd.hasOption("m")) {
                mode = cmd.getOptionValue("m");
            }

            if (cmd.hasOption("w")) {
                windowSize = Integer.parseInt(cmd.getOptionValue("w"));
            }

        } catch (ParseException e) {
            System.out.println("Failed to parse command line properties");
            help(options);
            return;
        }

        try {
            boolean usingFolder = cmd.hasOption("f");
            if (usingFolder) {
                inFilePath = folderPath;
            }
            if (mode.equals("p")) {
                handlePreprocess(inFilePath, usingFolder);

            } else if (mode.equals("s")) {
                handleSearch(inFilePath, usingFolder, windowSize);
            } else if (mode.equals("a")) {
                handlePreprocess(inFilePath, usingFolder);
                if (usingFolder) {
                    handleSearch(inFilePath, usingFolder, windowSize);
                } else {
                    handleSearch(inFilePath.replace(".xml", ".sblock.csv"), usingFolder, windowSize);
                }

            } else {
                System.out.println("Mode must be 'p' for preprocessing, 's' for search or 'a' for both");
                help(options);
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
//        SlidingWindow.findAnomalies(inFilePath, null, outFilePath, 10, 20);

    }

    private static void handlePreprocess(String inPath, boolean folder) throws IOException, ParserConfigurationException, SAXException {
        if (folder) {
            System.out.println("Preprocessing these files:");
            Files.newDirectoryStream(Paths.get(inPath),
                    path -> path.toString().endsWith(".xml"))
                    .forEach(path -> System.out.println(path.toString()));
            Preprocess.preprocess(inPath, true);
        } else {
            System.out.println("Preprocessing file: " + inPath);
            Preprocess.preprocess(inPath, false);
        }
    }

    private static void handleSearch(String inPath, boolean folder, int windowSize) throws IOException {
        if (folder) {
            System.out.println("Analysing these files:");
            Files.newDirectoryStream(Paths.get(inPath),
                    path -> path.toString().endsWith(".sblock.csv"))
                    .forEach(path -> System.out.println(path.toString()));
            Files.newDirectoryStream(Paths.get(inPath),
                    path -> path.toString().endsWith(".sblock.csv"))
                    .forEach(path -> {
                        try {
                            callFindAnomalies(path.toString(), windowSize);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });

        } else {
            callFindAnomalies(inPath, windowSize);
        }
    }

    private static void callFindAnomalies(String inPath, int windowSize) throws IOException{
        System.out.println("Analysing file: " + inPath);
        String outVectorsFilePath = inPath.replace(".sblock.csv", ".vec.csv");
        String outDistancesFilePath = inPath.replace(".sblock.csv", ".dist.csv");
        SlidingWindow.findAnomalies(inPath, outVectorsFilePath, outDistancesFilePath, windowSize, 1);
        System.out.println(inPath + " analysed. Results in: " + outDistancesFilePath +", feature vectors in: " + outVectorsFilePath);
    }

    private static void help(Options options) {
        HelpFormatter formater = new HelpFormatter();

        formater.printHelp("Main", options);
        System.exit(0);
    }


}