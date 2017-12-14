package preprocess;

import org.deeplearning4j.models.word2vec.Word2Vec;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import utils.Constants;
import utils.W2vVectorOperations;

import java.io.IOException;

public class W2vFirstReadSaxHandler extends DefaultHandler {
    private static Word2Vec word2vec;
    private StringBuilder content;
    private double[] sumW2vVector = new double[Constants.W2V_VECTOR_LEN];
    private INDArray wordMatrix;


    public W2vFirstReadSaxHandler(Word2Vec w2v) throws IOException {
        word2vec = w2v;
        content = new StringBuilder();
        for (int i = 0; i < Constants.W2V_VECTOR_LEN; i++) {
            sumW2vVector[i] = 0.0;
        }
    }

    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        content = new StringBuilder();
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("lemma")) {
            wordMatrix = word2vec.getWordVectorMatrix(content.toString());
            if (wordMatrix != null) {
                W2vVectorOperations.addAbsDoubleVectors(sumW2vVector, W2vVectorOperations.wordMatrixToDoubles(wordMatrix));
            }
        }
    }

    public void characters(char[] ch, int start, int length)
            throws SAXException {
        content.append(ch, start, length);
    }

    public void endDocument() throws SAXException {

    }

    public double[] getSumW2vVector() {
        return sumW2vVector;
    }
}
