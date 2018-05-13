package entities;

import org.deeplearning4j.models.word2vec.Word2Vec;
import org.nd4j.linalg.api.ndarray.INDArray;
import utils.Constants;
import utils.FrequencyMap;
import utils.W2vVectorOperations;

import java.util.Arrays;

public class ADSentenceBlock {
    private String header;
    private int id;
    private int startChar;
    private int endChar;
    private int sentences = 0;
    private int words = 0;
    private int chars = 0;
    private int syllables = 0;
    private int shortSentences = 0;
    private int longSentences = 0;
    private int shortWords = 0; //1 syllable
    private int longWords = 0; //3+ syllables
    private int sixCharWords = 0; //6+ chars
    private int passive = 0;
    private int questions = 0;
    private int[] sentimentArray = new int[5];
    private int[] nerArray = new int[6];
    private int[] freqArray = new int[6];
    private int[] posArray = new int[PosTags.getPosTagsLenght()];
    private double[] w2vArray = new double[Constants.W2V_VECTOR_LEN];
    private int startsWithCCorIN = 0;



    public ADSentenceBlock(int id, String header) {
        Arrays.fill(sentimentArray, 0);
        Arrays.fill(posArray, 0);
        this.header = header;
        this.id = id;
    }

    public ADSentenceBlock(InputSentence inSentence, String header, final Word2Vec word2Vec) {//, final int[] topIndexes) {
        Arrays.fill(sentimentArray, 0);
        Arrays.fill(posArray, 0);
        Arrays.fill(w2vArray, 0);
        boolean starting = true;
        this.header = header;
        this.id = inSentence.getId();
        this.startChar = inSentence.getStartChar();
        this.endChar = inSentence.getEndChar();
        sentences = 1;
        if (inSentence.getPassive()) {
            this.passive = 1;
        }
        this.sentimentArray[inSentence.getSentiment()]++;
        System.arraycopy(inSentence.getNerArray(), 0, this.nerArray, 0, nerArray.length);
        int syllables;
        int pos;
        INDArray wordMatrix;
        double[] wordVector;
        int freq;
        for (InputToken token : inSentence.getTokens()) {
            words++;
            chars += token.getCharacters();
            syllables = token.getSyllables();
            this.syllables += syllables;
            if (syllables > 2) {
                longWords++;
            }
            if (syllables == 1) {
                shortWords++;
            }
            if (token.getCharacters() > 5) {
                sixCharWords++;
            }
            if (token.getPOS().equals(".") && token.getLemma().equals("?")) {
                questions = 1;
            }
            freq = FrequencyMap.getFrequency(token.getLemma());
            if (freq < 0) {
                freqArray[5]++;
            } else if (freq < 1000) {
                freqArray[0]++;
            } else if (freq < 5000) {
                freqArray[1]++;
            } else if (freq < 10000) {
                freqArray[2]++;
            } else if (freq < 50000) {
                freqArray[3]++;
            } else if (freq > 0) {
                freqArray[4]++;
            }


            pos = PosTags.getPosIndex(token.getPOS());
            posArray[pos]++;
            if (starting && pos > 8) {
                if (pos == 9 || pos == 14) {
                    startsWithCCorIN = 1;
                }
                starting = false;
            }
            wordMatrix = word2Vec.getWordVectorMatrix(token.getLemma());
            if (wordMatrix != null) {
//                wordVector = W2vVectorOperations.getArrayByIndexes(wordMatrix, topIndexes);
//                w2vArray = W2vVectorOperations.addDoubleVectors(w2vArray, wordVector);
                wordVector = W2vVectorOperations.wordMatrixToDoubles(wordMatrix);
                w2vArray = W2vVectorOperations.addDoubleVectors(w2vArray, wordVector);
            }

        }
        shortSentences = (words < 8) ? 1 : 0;
        longSentences = (words > 15) ? 1 : 0;
    }

    public void loadCSVLine(String sourceCSVLine) {
        String[] brokenLine = sourceCSVLine.split(",");
        //String[] brokenLine = sourceCSVLine.replace(" ", "").split(",");
        for (int i = 1; i < 21 + PosTags.getPosTagsLenght() + w2vArray.length; i++) { //Keep " " in text
            brokenLine[i] = brokenLine[i].replace(" ", "");
        }
        header = brokenLine[0].replaceAll("\"", "");
        id = Integer.parseInt(brokenLine[1]);
        startChar = Integer.parseInt(brokenLine[2]);
        endChar = Integer.parseInt(brokenLine[3]);
        sentences = Integer.parseInt(brokenLine[4]);
        words = Integer.parseInt(brokenLine[5]);
        chars = Integer.parseInt(brokenLine[6]);
        syllables = Integer.parseInt(brokenLine[7]);
        shortSentences = Integer.parseInt(brokenLine[8]);
        longSentences = Integer.parseInt(brokenLine[9]);
        shortWords = Integer.parseInt(brokenLine[10]); //1 syllable
        longWords = Integer.parseInt(brokenLine[11]); //3+ syllables
        sixCharWords = Integer.parseInt(brokenLine[12]); //6+ chars
        passive = Integer.parseInt(brokenLine[13]);
        questions = Integer.parseInt(brokenLine[14]);
        startsWithCCorIN = Integer.parseInt(brokenLine[15]);
        for (int i = 0; i < sentimentArray.length; i++) {
            sentimentArray[i] = Integer.parseInt(brokenLine[16 + i]);
        }
        for (int i = 0; i < nerArray.length; i++) {
            nerArray[i] = Integer.parseInt(brokenLine[21 + i]);
        }
        for (int i = 0; i < freqArray.length; i++) {
            nerArray[i] = Integer.parseInt(brokenLine[27 + i]);
        }
        for (int i = 0; i < posArray.length; i++) {
            posArray[i] = Integer.parseInt(brokenLine[33 + i]);
        }
        for (int i = 0; i < w2vArray.length; i++) {
            w2vArray[i] = Double.valueOf(brokenLine[79 + i]);
        }

    }


    public void increase(ADSentenceBlock otherADSB) {
        if (startChar > otherADSB.startChar - 3) { startChar = otherADSB.startChar; }
        if (endChar < otherADSB.endChar + 3) { endChar = otherADSB.endChar; }
        sentences += otherADSB.sentences;
        words += otherADSB.words;
        chars += otherADSB.chars;
        syllables += otherADSB.syllables;
        shortSentences += otherADSB.shortSentences;
        longSentences += otherADSB.longSentences;
        shortWords += otherADSB.shortWords;
        longWords += otherADSB.longWords;
        sixCharWords += otherADSB.sixCharWords;
        passive += otherADSB.passive;
        questions += otherADSB.questions;
        startsWithCCorIN += otherADSB.startsWithCCorIN;
        for (int i = 0; i < sentimentArray.length; i++) {
            sentimentArray[i] += otherADSB.sentimentArray[i];
        }
        for (int i = 0; i < nerArray.length; i++) {
            nerArray[i] += otherADSB.nerArray[i];
        }
        for (int i = 0; i < freqArray.length; i++) {
            freqArray[i] += otherADSB.freqArray[i];
        }
        for (int i = 0; i < posArray.length; i++) {
            posArray[i] += otherADSB.posArray[i];
        }
        for (int i = 0; i < w2vArray.length; i++) {
            w2vArray[i] += otherADSB.w2vArray[i];
        }
    }

    public void decrease(ADSentenceBlock otherADSB) {
        if (Math.abs(startChar - otherADSB.startChar) <= 3 && endChar > otherADSB.endChar) { startChar = otherADSB.endChar - 1; } // take away the beginning
        if (Math.abs(endChar - otherADSB.endChar) <= 3 && startChar < otherADSB.startChar) { endChar = otherADSB.startChar - 1; } // take away the end
        sentences -= otherADSB.sentences;
        words -= otherADSB.words;
        chars -= otherADSB.chars;
        syllables -= otherADSB.syllables;
        shortSentences -= otherADSB.shortSentences;
        longSentences -= otherADSB.longSentences;
        shortWords -= otherADSB.shortWords;
        longWords -= otherADSB.longWords;
        sixCharWords -= otherADSB.sixCharWords;
        passive -= otherADSB.passive;
        questions -= otherADSB.questions;
        startsWithCCorIN -= otherADSB.startsWithCCorIN;
        for (int i = 0; i < sentimentArray.length; i++) {
            sentimentArray[i] -= otherADSB.sentimentArray[i];
        }
        for (int i = 0; i < nerArray.length; i++) {
            nerArray[i] -= otherADSB.nerArray[i];
        }
        for (int i = 0; i < freqArray.length; i++) {
            freqArray[i] -= otherADSB.freqArray[i];
        }
        for (int i = 0; i < posArray.length; i++) {
            posArray[i] -= otherADSB.posArray[i];
        }
        for (int i = 0; i < w2vArray.length; i++) {
            w2vArray[i] -= otherADSB.w2vArray[i];
        }
    }

    public void replace(ADSentenceBlock otherADSB) {
        sentences = otherADSB.sentences;
        words = otherADSB.words;
        chars = otherADSB.chars;
        syllables = otherADSB.syllables;
        shortSentences = otherADSB.shortSentences;
        longSentences = otherADSB.longSentences;
        shortWords = otherADSB.shortWords;
        longWords = otherADSB.longWords;
        sixCharWords = otherADSB.sixCharWords;
        passive = otherADSB.passive;
        questions = otherADSB.questions;
        startsWithCCorIN = otherADSB.startsWithCCorIN;
        System.arraycopy(otherADSB.sentimentArray, 0, sentimentArray, 0, sentimentArray.length);
        System.arraycopy(otherADSB.nerArray, 0, nerArray, 0, nerArray.length);
        System.arraycopy(otherADSB.freqArray, 0, freqArray, 0, freqArray.length);
        System.arraycopy(otherADSB.posArray, 0, posArray, 0, posArray.length);
        System.arraycopy(otherADSB.w2vArray, 0, w2vArray, 0, w2vArray.length);
    }


    @Override
    public String toString() {
        return "{\"ADSentenceBlock\": {" +
                "\"header\": " + header + "," +
                "\"id\": " + Integer.toString(id) + "," +
                "\"startChar\": " + Integer.toString(startChar) + "," +
                "\"endChar\": " + Integer.toString(endChar) + "," +
                "\"sentences\": " + Integer.toString(sentences) + "," +
                "\"words\": " + Integer.toString(words) + "," +
                "\"chars\": " + Integer.toString(chars) + "," +
                "\"syllables\": " + Integer.toString(syllables) + "," +
                "\"shortSentences\": " + Integer.toString(shortSentences) + "," +
                "\"longSentences\": " + Integer.toString(longSentences) + "," +
                "\"shortWords\": " + Integer.toString(shortWords) + "," +
                "\"longWords\": " + Integer.toString(longWords) + "," +
                "\"sixCharWords\": " + Integer.toString(sixCharWords) + "," +
                "\"passive\": " + Integer.toString(passive) + "," +
                "\"questions\": " + Integer.toString(questions) + "," +
                "\"startsWithCCorIN\": " + Integer.toString(questions) + "," +
                "\"sentimentArray\": " + Arrays.toString(sentimentArray) + "," +
                "\"nerArray\": " + Arrays.toString(nerArray) + "," +
                "\"freqArray\": " + Arrays.toString(freqArray) + "," +
                "\"posArray\": " + Arrays.toString(posArray) + "," +
                "\"w2vArray\": " + Arrays.toString(w2vArray) +
                "}}";
    }

    public String toJSON() {
        return this.toString();
    }

    public String toCSVLine() {
        return "\"" + header + "\"," +
                Integer.toString(id) + "," +
                Integer.toString(startChar) + "," +
                Integer.toString(endChar) + "," +
                Integer.toString(sentences) + "," +
                Integer.toString(words) + "," +
                Integer.toString(chars) + "," +
                Integer.toString(syllables) + "," +
                Integer.toString(shortSentences) + "," +
                Integer.toString(longSentences) + "," +
                Integer.toString(shortWords) + "," +
                Integer.toString(longWords) + "," +
                Integer.toString(sixCharWords) + "," +
                Integer.toString(passive) + "," +
                Integer.toString(questions) + "," +
                Integer.toString(startsWithCCorIN) + "," +
                Arrays.toString(sentimentArray).replace("[", "").replace("]", "") + "," +
                Arrays.toString(nerArray).replace("[", "").replace("]", "") + "," +
                Arrays.toString(freqArray).replace("[", "").replace("]", "") + "," +
                Arrays.toString(posArray).replace("[", "").replace("]", "") + "," +
                Arrays.toString(w2vArray).replace("[", "").replace("]", "");

    }


    public static String getCSVHeader() {
        String w2vHeader = "";
        for (int i = 0; i < Constants.W2V_NUM_IN_SENTENCEBLOCK; i++) {
            w2vHeader += ", w2v" + Integer.toString(i);
        }
        return "header, id, startChar, endChar, sentences, words, chars, syllables, shortSentences, longSentences, shortWords, longWords," +
                " sixCharWords, passive, questions, startsWithCCorIN, sentiment0, sentiment1, sentiment2, sentiment3, sentiment4, nerPER," +
                " nerLOC, nerORG, nerNUM, nerTIME, nerMISC, f1k, f5k, f10k, f50k, f100k, f100k+" +
                PosTags.getCSVHeaderString() + w2vHeader;
    }

        public String getHeader() {
        return header;
    }
    public int getId() {
        return id;
    }

    public int getStartChar() {
        return startChar;
    }

    public int getEndChar() {
        return endChar;
    }

    public int getSentences() {
        return sentences;
    }

    public int getWords() {
        return words;
    }

    public int getChars() {
        return chars;
    }

    public int getSyllables() {
        return syllables;
    }

    public int getShortSentences() {
        return shortSentences;
    }

    public int getLongSentences() {
        return longSentences;
    }

    public int getShortWords() {
        return shortWords;
    }

    public int getLongWords() {
        return longWords;
    }

    public int getSixCharWords() {
        return sixCharWords;
    }

    public int getPassive() {
        return passive;
    }

    public int getQuestions() {
        return questions;
    }

    public int getStartsWithCCorIN() {
        return startsWithCCorIN;
    }

    public int[] getSentimentArray() {
        return sentimentArray;
    }

    public int[] getNerArray() {
        return nerArray;
    }

    public int[] getFreqArray() {
        return freqArray;
    }

    public int[] getPosArray() {
        return posArray;
    }

    public double[] getW2vArray() {
        return w2vArray;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStartChar(int startChar) {
        this.startChar = startChar;
    }

    public void setEndChar(int endChar) {
        this.endChar = endChar;
    }
}
