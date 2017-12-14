package entities;

import java.util.Arrays;

public class ADVector {
    public static final int VECTOR_LEN = 71;
    private String header;
    private int id;
    private String text = "";
    private double[] computeVector = new double[VECTOR_LEN];

    public ADVector() {}

    public ADVector(ADSentenceBlock sBlock) {
        this.loadSentenceBlock(sBlock);
    }

    public void loadSentenceBlock(ADSentenceBlock sBlock) {
        this.header = sBlock.getHeader();
        this.id = sBlock.getId();
        int[] posArray = sBlock.getPosArray();
        double sentences = (double) sBlock.getSentences();
        double words = (double) sBlock.getWords();
        double chars = (double) sBlock.getChars();
        double syllables = (double) sBlock.getSyllables();
        //Simple surface metrics
        computeVector[0] = words / sentences; //Avg sentence length
        computeVector[1] = chars / words; //Avg word length
        computeVector[2] = syllables / words; //Avg syllables/word
        computeVector[3] = (double) sBlock.getShortSentences() / sentences; //Percentage of short sentences (<8 words)
        computeVector[4] = (double) sBlock.getLongSentences() / sentences; //Percentage of long sentences (16+ words)
        computeVector[5] = (double) sBlock.getShortWords() / words; //Percentage of short words (1 syllable)
        computeVector[6] = (double) sBlock.getLongWords() / words; //Percentage of long words (3+ syllables)
        computeVector[7] = (double) sBlock.getSixCharWords() / words; //Percentage of words with 6+ chars
        computeVector[8] = (double) sBlock.getPassive() / words; //Percentage of passive sentences
        computeVector[9] = (double) sBlock.getQuestions() / sentences; //Percentage of sentences that are questions
        computeVector[10] = (double) sBlock.getStartsWithCCorIN() / sentences; //Sentences starting with CC or IN
        double puncChars = 0.0;
        for (int i = 0; i <= 8; i++) {
            puncChars += (double) posArray[i];
        }
        computeVector[11] = puncChars / chars; //Percentage of punctuation characters
        computeVector[12] = (double) posArray[8] / chars; //Percentage of chars that are ':', ';', '...'
        computeVector[13] = (double) posArray[5] / chars; //Percentage of chars that are ','
        //Not using word tokens/words

        //Readability metrics
        computeVector[14] = 206.835 - 1.015 * (words / sentences) - 84.6 * (syllables / words); //Flesch-Kincaid Reading Ease
        computeVector[15] = 11.8 * (syllables / words) + 0.39 * (words / sentences) - 15.59; //Flesch-Kincaid Grade Level
        computeVector[16] = (words / sentences) + (double) sBlock.getLongWords() / words; //Gunning-Fog Index
        computeVector[17] = 5.89 * (chars / words) - 0.3 * (sentences / words) / 100.0 - 15.8; //Coleman-Liau Formula
        computeVector[18] = 4.71 * (chars / words) + 0.5 * (words / sentences) - 21.43; //Automated Readability Index
        computeVector[19] = (words / sentences) + 100.0 * ((double) sBlock.getSixCharWords() / words); // Lix Formula
        computeVector[20] = 3 + Math.sqrt((double) sBlock.getLongWords() * 30 / sentences); //SMOG Index

        //Sentiment metrics
        int[] sentimentArray = sBlock.getSentimentArray();
        for (int i = 0; i < 5; i++) {
            computeVector[21 + i] = (double) sentimentArray[i] / sentences;
        }

        //POS metrics
        for(int i = 0; i < posArray.length; i++) {
            computeVector[25 + i] = (double) posArray[i] / words;
        }
    }

    public void normalize(double[][] normVectors) {
        double[] mins = normVectors[0];
        double[] dispersions = normVectors[1];
        for (int i = 0; i < VECTOR_LEN; i++) {
            computeVector[i] = (computeVector[i] - mins[i]) / dispersions[i];
        }
    }

    public double manhattanDistanceTo(ADVector other) {
        double[] otherVector = other.getComputeVector();
        double distance = 0.0;
        for (int i = 0; i < VECTOR_LEN; i++) {
            distance += Math.abs(this.computeVector[i] - otherVector[i]);
        }
        return distance;
    }

    public String toCSVLine() {
        return "\"" + header + "\"," +
                Integer.toString(id) + "," +
                Arrays.toString(computeVector).replace("[", "").replace("]", "");// +
                //",\" " + text + "\"";
    }

    public String differenceToCSVLine(ADVector other) {
        String resultString = "\"" + header + "\"," +
                Integer.toString(id);
        double[] otherVector = other.getComputeVector();
        double distance = 0.0;
        for (int i = 0; i < VECTOR_LEN; i++) {
            distance += Math.abs(this.computeVector[i] - otherVector[i]);
            resultString += "," + Double.toString(Math.abs(this.computeVector[i] - otherVector[i]));
        }
        resultString += ",\" " + text + "\"";
        resultString += "," + Double.toString(distance);
        return resultString;
    }

    public String cosineDistanceToCSVLine(ADVector other) {
        String resultString = "\"" + header + "\"," +
                Integer.toString(id);
        double[] otherVector = other.getComputeVector();
        resultString += ",\" " + text + "\"";
        double multiplySum = 0.0;
        double thisSqrtSum = 0.0;
        double otherSqrtSum = 0.0;
        for (int i = 0; i < VECTOR_LEN; i++) {
            multiplySum += this.computeVector[i] * otherVector[i];
            thisSqrtSum += this.computeVector[i] * this.computeVector[i];
            otherSqrtSum += otherVector[i] * otherVector[i];
        }
        double distance = 1 - (multiplySum / (Math.sqrt(thisSqrtSum) * Math.sqrt(otherSqrtSum)));
        resultString += "," + Double.toString(distance);
        return resultString;
    }

    public double[] getComputeVector() {
        return computeVector;
    }

    public static String getCSVHeader() {
        String posTags = PosTags.getCSVHeaderString();
        posTags = posTags.substring(0,posTags.length() - 1);
        return "header, id, sentenceLength, wordLength, syllablesPerWord, shortSentences, longSentences, shortWords, longWords, " +
                "sixCharWords, passive, questions, startsWithCCorIN, puncChars, chars, commas," +
                "Flesch-Kincaid Reading Ease, Flesch-Kincaid Grade Level, Gunning-Fog Index, Coleman-Liau Formula, Automated Readability Index, Lix Formula, SMOG index" +
                "sentiment0, sentiment1, sentiment2, sentiment3, sentiment4, " + posTags;// + " text";

    }

    public static String getShortHeader() {
        return "header, id, text, distance";
    }
}

