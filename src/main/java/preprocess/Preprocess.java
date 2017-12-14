package preprocess;

import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.omg.CORBA.CODESET_INCOMPATIBLE;
import org.xml.sax.SAXException;
import utils.Constants;
import utils.W2vVectorOperations;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Preprocess {
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        Word2Vec word2vec;

        long yourmilliseconds = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date resultdate = new Date(yourmilliseconds);

        System.out.println("Loading Word2Vec. Time: " + sdf.format(resultdate));

        File gModel = new File("GoogleNews-vectors-negative300.bin");
        word2vec = WordVectorSerializer.readWord2VecModel(gModel);

//                // Gets Path to Text file
//        // Gets Path to Text file
//        String filePath = new File("resources\\raw_sentences.txt").getAbsolutePath();
//
//        // Strip white space before and after for each line
//        SentenceIterator iter = new BasicLineIterator(filePath);
//        // Split on white spaces in the line to get words
//        TokenizerFactory t = new DefaultTokenizerFactory();
//
//        /*
//            CommonPreprocessor will apply the following regex to each token: [\d\.:,"'\(\)\[\]|/?!;]+
//            So, effectively all numbers, punctuation symbols and some special symbols are stripped off.
//            Additionally it forces lower case for all tokens.
//         */
//        t.setTokenPreProcessor(new CommonPreprocessor());
//
//        word2vec = new Word2Vec.Builder()
//                .minWordFrequency(5)
//                .iterations(1)
//                .layerSize(300)
//                .seed(42)
//                .windowSize(5)
//                .iterate(iter)
//                .tokenizerFactory(t)
//                .build();
//
//        word2vec.fit();

        yourmilliseconds = System.currentTimeMillis();
        resultdate = new Date(yourmilliseconds);

        System.out.println("Finished Loading Word2Vec. Time: " + sdf.format(resultdate));

        SAXParserFactory factory;
        InputStream xmlInput;
        SAXParser saxParser;
        W2vFirstReadSaxHandler w2vFirstReadSaxHandler;
        StanfordNLPSaxHandler stanfordNLPSaxHandler;
        int[] topIndexes = null;
        ArrayList<String> filePaths =  new ArrayList<>();
        Files.newDirectoryStream(Paths.get(".\\resources\\test"),
                path -> path.toString().endsWith(".xml"))
                .forEach(path -> filePaths.add(path.toString()));
        String fileName;
        int i = 0;
        for (String myFilePath : filePaths) {
            fileName = myFilePath.substring(myFilePath.lastIndexOf("\\"));

            yourmilliseconds = System.currentTimeMillis();
            resultdate = new Date(yourmilliseconds);
            if (myFilePath.contains("1")) {
                System.out.println("Preparing w2v choice for: " + fileName + ", Time: " + sdf.format(resultdate));
                factory = SAXParserFactory.newInstance();
                xmlInput = new FileInputStream(myFilePath);
                saxParser = factory.newSAXParser();
                w2vFirstReadSaxHandler = new W2vFirstReadSaxHandler(word2vec);
                saxParser.parse(xmlInput, w2vFirstReadSaxHandler);
                topIndexes = W2vVectorOperations.getTopIndexes(w2vFirstReadSaxHandler.getSumW2vVector(), Constants.W2V_NUM_IN_SENTENCEBLOCK);
            }
            yourmilliseconds = System.currentTimeMillis();
            resultdate = new Date(yourmilliseconds);
            System.out.println("Parsing file: " + fileName + ", Time: " + sdf.format(resultdate));

            factory = SAXParserFactory.newInstance();
            xmlInput = new FileInputStream(myFilePath);
            saxParser = factory.newSAXParser();
            stanfordNLPSaxHandler = new StanfordNLPSaxHandler(myFilePath, myFilePath + "-sblock.csv", word2vec, topIndexes);
            saxParser.parse(xmlInput, stanfordNLPSaxHandler);

            yourmilliseconds = System.currentTimeMillis();
            resultdate = new Date(yourmilliseconds);
            System.out.println("Finished. Output file: " + fileName + "-sblock.csv , Time: " + sdf.format(resultdate));
        }
    }
}
