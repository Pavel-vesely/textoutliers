import detection.Anomaly;
import detection.AnomalyTreeSet;
import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.Tokenizer;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.TreeSet;
import java.util.stream.Stream;

public class Test {
    private static Word2Vec word2vec;
    private static Double[] sumVector;
    public static void main(String[] args) throws IOException {
//        sumVector = new Double[300];
//        for (int i = 0; i < 300; i++) {
//            sumVector[i] = 0.0;
//        }
//        File gModel = new File("GoogleNews-vectors-negative300.bin");
//        word2vec = WordVectorSerializer.readWord2VecModel(gModel);
//
//
//        Collection<String> lst = word2vec.wordsNearest("day", 10);
//        System.out.println("10 Words closest to 'day': " + lst);
//        System.out.println(word2vec.getWordVectorMatrix("day"));
//        System.out.println(word2vec.getWordVectorMatrix("player"));
//        INDArray arr = word2vec.getWordVectorMatrix("day");
//        System.out.println(arr.getDouble(299));
//
//        try (Stream<Path> paths = Files.walk(Paths.get("resources\\suspicious-documents"))) {
//            paths
//                .filter(Files::isRegularFile)
//                .filter(p -> p.toString().endsWith(".txt"))
//                .forEach(p -> parseFile(p));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        parseFile(Paths.get("resources\\suspicious-documents\\part1\\suspicious-document00001.txt"));





//        File[] fileList = new File("\\resources\\out").listFiles();
//        String filePath;
//        for (File file : fileList) {
//            filePath = file.getPath();
//            System.out.print(filePath);
//            if (filePath.contains(".txt.xml")) {
//                System.out.print(" ***");
//            }
//            System.out.println();
//        }

        int max = 202;
        double chance = 0.0;
        for (double i = 1.1; i < 21; i = i + 1.0) {
            chance += i / (max - i);
            System.out.println();
        }

        ArrayList<Anomaly> list = new ArrayList<>();
        list.add(new Anomaly(1,1,50,0.5));
        list.add(new Anomaly(2,100,150, 0.6));
        list.add(new Anomaly(3, 101, 151, 0.61));
        list.add(new Anomaly(4, 123, 200, 0.8));
        list.add(new Anomaly(5, 240, 300, 0.2));
        list.add(new Anomaly(6, 300, 500, 0.9));
        int setSize = 3;
        AnomalyTreeSet anomalies = new AnomalyTreeSet();
        anomalies.add(new Anomaly(-1, -1, 0, -1.0));
        anomalies.add(new Anomaly(-1, -2, 0, -2.0));
        anomalies.add(new Anomaly(-1, -3, 0, -3.0));
        for (Anomaly item : list) {
            anomalies.updateSet(item);
            anomalies.forEach(System.out::println);
            System.out.println();
        }

    }

    public static TreeSet<Anomaly> update(TreeSet<Anomaly> set, Anomaly item) {
        if (item.getDistance() < set.first().getDistance()) {
            return set;
        }
        Anomaly overlapping = null;
        for (Anomaly setItem : set) {
            if (item.overlaps(setItem)) {
                overlapping = setItem;
                break;
            }
        }
        if (overlapping != null) {
            if(overlapping.getDistance() < item.getDistance()) {
                set.remove(overlapping);
                set.add(item);
                return set;
            }
        }
        set.remove(set.first());
        set.add(item);
        return set;
    }



    public static void parseFile(Path path) throws RuntimeException {
        System.out.println(path);
        try {
            Reader reader = new FileReader(new File(path.toString()));
            StreamTokenizer tokenizer = new StreamTokenizer(reader);
            String token;
            Double[] vector;
            INDArray wordMatrix;
            while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
                token = tokenizer.sval;
                if (token == null) { continue; }
                token = token.replace(".", "");
                wordMatrix = word2vec.getWordVectorMatrix(token);
                if (wordMatrix != null) {
                    vector = new Double[300];
                    for (int i = 0; i < 300; i++) {
                        vector[i] = wordMatrix.getDouble(i);
                    }
                    addVector(vector);
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            printSumVector();
        }
    }

    public static void addVector(Double[] newVector) {
        if (newVector.length != 300) { return; }
        for (int i = 0; i < 300; i++) {
            sumVector[i] += Math.abs(newVector[i]);
        }
    }

    public static void printSumVector() {
        for (int i = 0; i < 300; i++) {
            System.out.print(sumVector[i]);
            System.out.print(", ");
        }
        System.out.println();
    }
}
