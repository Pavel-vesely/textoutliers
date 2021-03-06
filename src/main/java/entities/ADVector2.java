package entities;

import utils.Constants;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

public class ADVector2 {
    private double[] surfaceVector = new double[21];
    private double[] sentimentVector = new double[5];
    private double[] wordClassVector = new double[12];
    private double[] w2vFeatureVector = new double[Constants.W2V_VECTOR_LEN];
    private int startChar = 0;
    private int endChar = 0;
    private String header = "";
    private int id = 0;
    private static int[] selectedW2VIndices;

    private void initiateSelectedW2VIndices(){
        if (selectedW2VIndices != null) {
            return;
        }
        int[] indices = new int[Constants.W2V_VECTOR_LEN];
        for (int i = 0; i < Constants.W2V_VECTOR_LEN; i++) {
            indices[i] = i;
        }
        selectedW2VIndices = indices;
    }

    public static void setSelectedW2VIndices(int[] newIndices) throws IllegalArgumentException {
        int[] newIndicesCopy = Arrays.copyOf(newIndices, newIndices.length);
        if (newIndicesCopy == null) { throw new IllegalArgumentException(); }
        if (newIndicesCopy.length < 1 || newIndicesCopy.length > Constants.W2V_VECTOR_LEN) { throw new IllegalArgumentException(); }
        if (newIndicesCopy[0] < 0 || newIndicesCopy[newIndicesCopy.length - 1] >= Constants.W2V_VECTOR_LEN) { throw new IllegalArgumentException(); }

        int last = newIndicesCopy[0];
        for (int i = 1; i < newIndicesCopy.length; i++) {
            if (newIndicesCopy[i] <= last) {
                throw new IllegalArgumentException();
            }
            last = newIndicesCopy[i];
        }

        selectedW2VIndices = newIndicesCopy;
    }

    public ADVector2() {
        initiateSelectedW2VIndices();
        Arrays.fill(surfaceVector, 0.0);
        Arrays.fill(sentimentVector, 0.0);
        Arrays.fill(wordClassVector, 0.0);
        Arrays.fill(w2vFeatureVector, 0.0);
    }

    public ADVector2(ADSentenceBlock sBlock) {
        initiateSelectedW2VIndices();
        header = sBlock.getHeader();
        id = sBlock.getId();
        startChar = sBlock.getStartChar();
        endChar = sBlock.getEndChar();
        loadSurfaceFeatures(sBlock);
        loadSentimentFeatures(sBlock);
        loadWordClasssFeatures(sBlock);
        loadW2vFeatures(sBlock);
    }

    public void loadSentenceBlock(ADSentenceBlock sBlock) {
        header = sBlock.getHeader();
        id = sBlock.getId();
        startChar = sBlock.getStartChar();
        endChar = sBlock.getEndChar();
        loadSurfaceFeatures(sBlock);
        loadSentimentFeatures(sBlock);
        loadWordClasssFeatures(sBlock);
        loadW2vFeatures(sBlock);
    }

    public void loadSurfaceFeatures(ADSentenceBlock sBlock) {
        double sentences = (double) sBlock.getSentences();
        double words = (double) sBlock.getWords();
        double chars = (double) sBlock.getChars();
        double syllables = (double) sBlock.getSyllables();

        int[] posArray = sBlock.getPosArray();
        //Simple surface metrics
        surfaceVector[0] = words / sentences; //Avg sentence length
        surfaceVector[1] = chars / words; //Avg word length
        surfaceVector[2] = syllables / words; //Avg syllables/word
        surfaceVector[3] = (double) sBlock.getShortSentences() / sentences; //Percentage of short sentences (<8 words)
        surfaceVector[4] = (double) sBlock.getLongSentences() / sentences; //Percentage of long sentences (16+ words)
        surfaceVector[5] = (double) sBlock.getShortWords() / words; //Percentage of short words (1 syllable)
        surfaceVector[6] = (double) sBlock.getLongWords() / words; //Percentage of long words (3+ syllables)
        surfaceVector[7] = (double) sBlock.getSixCharWords() / words; //Percentage of words with 6+ chars
        surfaceVector[8] = (double) sBlock.getPassive() / words; //Percentage of passive sentences
        surfaceVector[9] = (double) sBlock.getQuestions() / sentences; //Percentage of sentences that are questions
        surfaceVector[10] = (double) sBlock.getStartsWithCCorIN() / sentences; //Sentences starting with CC or IN
        double puncChars = 0.0;
        for (int i = 0; i <= 8; i++) {
            puncChars += (double) posArray[i];
        }
        surfaceVector[11] = puncChars / chars; //Percentage of punctuation characters
        surfaceVector[12] = (double) posArray[8] / chars; //Percentage of chars that are ':', ';', '...'
        surfaceVector[13] = (double) posArray[5] / chars; //Percentage of chars that are ','
        //Not using word tokens/words

        //Readability metrics
        surfaceVector[14] = 206.835 - 1.015 * (words / sentences) - 84.6 * (syllables / words); //Flesch-Kincaid Reading Ease
        surfaceVector[15] = 11.8 * (syllables / words) + 0.39 * (words / sentences) - 15.59; //Flesch-Kincaid Grade Level
        surfaceVector[16] = (words / sentences) + (double) sBlock.getLongWords() / words; //Gunning-Fog Index
        surfaceVector[17] = 5.89 * (chars / words) - 0.3 * (sentences / words) / 100.0 - 15.8; //Coleman-Liau Formula
        surfaceVector[18] = 4.71 * (chars / words) + 0.5 * (words / sentences) - 21.43; //Automated Readability Index
        surfaceVector[19] = (words / sentences) + 100.0 * ((double) sBlock.getSixCharWords() / words); // Lix Formula
        surfaceVector[20] = 3 + Math.sqrt((double) sBlock.getLongWords() * 30 / sentences); //SMOG Index
    }

    public void loadSentimentFeatures(ADSentenceBlock sBlock) {
        double sentences = (double) sBlock.getSentences();
        int[] sentimentArray = sBlock.getSentimentArray();
        for (int i = 0; i < 5; i++) {
            sentimentVector[i] = (double) sentimentArray[i] / sentences;
        }
    }

    public void loadWordClasssFeatures(ADSentenceBlock sBlock) {
        int[] posArray = sBlock.getPosArray();
        double words = (double) sBlock.getWords();
        wordClassVector[0] = (posArray[20] + posArray[21] + posArray[22] + posArray[23]) / words; //Nouns
        wordClassVector[1] = (posArray[35] + posArray[36] + posArray[37] + posArray[38] + posArray[39] + posArray[40]) / words; //Verbs
        wordClassVector[2] = (posArray[15] + posArray[16] + posArray[17]) / words; //Adjectives
        wordClassVector[3] = (posArray[28] + posArray[29] + posArray[30]) / words; //Adverbs
        wordClassVector[4] = (posArray[26] + posArray[27] + posArray[42] + posArray[43]) / words; //Pronouns
        wordClassVector[5] = (posArray[14]) / words; //Prepositions
        wordClassVector[6] = (posArray[9]) / words; //Conjunctions
        wordClassVector[7] = (posArray[11]) / words; //Determiner
        wordClassVector[8] = (posArray[41] + posArray[42] + posArray[43] + posArray[44]) / words; //WH-words
        wordClassVector[1] = (posArray[35] + posArray[36] + posArray[37] + posArray[38] + posArray[39] + posArray[40]) / words; //Verbs
    }

    public void loadW2vFeatures(ADSentenceBlock sBlock) {
        double[] w2vArray = sBlock.getW2vArray();
        for (int i = 0; i < w2vArray.length; i++) {
            w2vFeatureVector[i] = w2vArray[i];
        }
    }

    public void normalize(ADVector2 minVec, ADVector2 maxVec) {
        for (int i = 0; i < surfaceVector.length; i++) {
            surfaceVector[i] = (surfaceVector[i] - minVec.surfaceVector[i]) / (maxVec.surfaceVector[i] - minVec.surfaceVector[i]);
        }
        for (int i = 0; i < sentimentVector.length; i++) {
            sentimentVector[i] = (sentimentVector[i] - minVec.sentimentVector[i]) / (maxVec.sentimentVector[i] - minVec.sentimentVector[i]);
        }
        for (int i = 0; i < wordClassVector.length; i++) {
            wordClassVector[i] = (wordClassVector[i] - minVec.wordClassVector[i]) / (maxVec.wordClassVector[i] - minVec.wordClassVector[i]);
        }
        for (int i = 0; i < w2vFeatureVector.length; i++) {
            w2vFeatureVector[i] = (w2vFeatureVector[i] - minVec.w2vFeatureVector[i]) / (maxVec.w2vFeatureVector[i] - minVec.w2vFeatureVector[i]);
        }
    }

    public void normalizeNoW2v(ADVector2 minVec, ADVector2 maxVec) {
        for (int i = 0; i < surfaceVector.length; i++) {
            surfaceVector[i] = (surfaceVector[i] - minVec.surfaceVector[i]) / (maxVec.surfaceVector[i] - minVec.surfaceVector[i]);
        }
        for (int i = 0; i < sentimentVector.length; i++) {
            sentimentVector[i] = (sentimentVector[i] - minVec.sentimentVector[i]) / (maxVec.sentimentVector[i] - minVec.sentimentVector[i]);
        }
        for (int i = 0; i < wordClassVector.length; i++) {
            wordClassVector[i] = (wordClassVector[i] - minVec.wordClassVector[i]) / (maxVec.wordClassVector[i] - minVec.wordClassVector[i]);
        }
    }

    public String cosineDistancesToCSVLine(ADVector2 other) {
        String resultString = "\"" + header + "\"," +
                Integer.toString(id) + ", " + Integer.toString(startChar) + ", " + Integer.toString(endChar);

        resultString += ", " + Double.toString(getCosineDistance(other));
        resultString += ", " + Double.toString(getCosineDistanceNoW2V(other));
        resultString += ", " + Double.toString(getCosineDistanceOnlyW2V(other));
        return resultString;
    }

    public double getCosineDistance(ADVector2 other) {
        double multiplySum = 0.0;
        double thisSqrtSum = 0.0;
        double otherSqrtSum = 0.0;
        for (int i = 0; i < surfaceVector.length; i++) {
            multiplySum += this.surfaceVector[i] * other.surfaceVector[i];
            thisSqrtSum += this.surfaceVector[i] * this.surfaceVector[i];
            otherSqrtSum += other.surfaceVector[i] * other.surfaceVector[i];
        }
        for (int i = 0; i < sentimentVector.length; i++) {
            multiplySum += this.sentimentVector[i] * other.sentimentVector[i];
            thisSqrtSum += this.sentimentVector[i] * this.sentimentVector[i];
            otherSqrtSum += other.sentimentVector[i] * other.sentimentVector[i];
        }
        for (int i = 0; i < wordClassVector.length; i++) {
            multiplySum += this.wordClassVector[i] * other.wordClassVector[i];
            thisSqrtSum += this.wordClassVector[i] * this.wordClassVector[i];
            otherSqrtSum += other.wordClassVector[i] * other.wordClassVector[i];
        }

        int j = 0;
        for (int i = 0; i < w2vFeatureVector.length; i++) {
            if (j < selectedW2VIndices.length && selectedW2VIndices[j] == i) {
                j++;
                multiplySum += this.w2vFeatureVector[i] * other.w2vFeatureVector[i];
                thisSqrtSum += this.w2vFeatureVector[i] * this.w2vFeatureVector[i];
                otherSqrtSum += other.w2vFeatureVector[i] * other.w2vFeatureVector[i];
            }
        }
        double distance = 1 - (multiplySum / (Math.sqrt(thisSqrtSum) * Math.sqrt(otherSqrtSum)));
        return distance;
    }

    public double getCosineDistanceNoW2V(ADVector2 other) {
        double multiplySum = 0.0;
        double thisSqrtSum = 0.0;
        double otherSqrtSum = 0.0;
        for (int i = 0; i < surfaceVector.length; i++) {
            multiplySum += this.surfaceVector[i] * other.surfaceVector[i];
            thisSqrtSum += this.surfaceVector[i] * this.surfaceVector[i];
            otherSqrtSum += other.surfaceVector[i] * other.surfaceVector[i];
        }
        for (int i = 0; i < sentimentVector.length; i++) {
            multiplySum += this.sentimentVector[i] * other.sentimentVector[i];
            thisSqrtSum += this.sentimentVector[i] * this.sentimentVector[i];
            otherSqrtSum += other.sentimentVector[i] * other.sentimentVector[i];
        }
        for (int i = 0; i < wordClassVector.length; i++) {
            multiplySum += this.wordClassVector[i] * other.wordClassVector[i];
            thisSqrtSum += this.wordClassVector[i] * this.wordClassVector[i];
            otherSqrtSum += other.wordClassVector[i] * other.wordClassVector[i];
        }
        double distance = 1 - (multiplySum / (Math.sqrt(thisSqrtSum) * Math.sqrt(otherSqrtSum)));
        return distance;
    }

    public double getCosineDistanceOnlyW2V(ADVector2 other) {
        double multiplySum = 0.0;
        double thisSqrtSum = 0.0;
        double otherSqrtSum = 0.0;
        int j = 0;
        for (int i = 0; i < w2vFeatureVector.length; i++) {
            if (j < selectedW2VIndices.length && selectedW2VIndices[j] == i) {
                j++;
                multiplySum += this.w2vFeatureVector[i] * other.w2vFeatureVector[i];
                thisSqrtSum += this.w2vFeatureVector[i] * this.w2vFeatureVector[i];
                otherSqrtSum += other.w2vFeatureVector[i] * other.w2vFeatureVector[i];
            }
        }
        double distance = 1 - (multiplySum / (Math.sqrt(thisSqrtSum) * Math.sqrt(otherSqrtSum)));
        return distance;
    }

    public double getRandomDistance(ADVector2 other) {
        SecureRandom rnd = new SecureRandom();
        return rnd.nextDouble();
    }

    public static String getShortHeader() {
        return "header, id, startChar, endChar, allDistance, noW2VDistance, onlyW2VDistance";
    }

    public void setLesser(ADVector2 other) {
        for (int i = 0; i < surfaceVector.length; i++) {
            surfaceVector[i] = this.surfaceVector[i] < other.surfaceVector[i] ? this.surfaceVector[i] : other.surfaceVector[i];
        }
        for (int i = 0; i < sentimentVector.length; i++) {
            sentimentVector[i] = this.sentimentVector[i] < other.sentimentVector[i] ? this.sentimentVector[i] : other.sentimentVector[i];
        }
        for (int i = 0; i < wordClassVector.length; i++) {
            wordClassVector[i] = this.wordClassVector[i] < other.wordClassVector[i] ? this.wordClassVector[i] : other.wordClassVector[i];
        }
        for (int i = 0; i < w2vFeatureVector.length; i++) {
            w2vFeatureVector[i] = this.w2vFeatureVector[i] < other.w2vFeatureVector[i] ? this.w2vFeatureVector[i] : other.w2vFeatureVector[i];
        }
    }

    public void setGreater(ADVector2 other) {
        for (int i = 0; i < surfaceVector.length; i++) {
            surfaceVector[i] = this.surfaceVector[i] > other.surfaceVector[i] ? this.surfaceVector[i] : other.surfaceVector[i];
        }
        for (int i = 0; i < sentimentVector.length; i++) {
            sentimentVector[i] = this.sentimentVector[i] > other.sentimentVector[i] ? this.sentimentVector[i] : other.sentimentVector[i];
        }
        for (int i = 0; i < wordClassVector.length; i++) {
            wordClassVector[i] = this.wordClassVector[i] > other.wordClassVector[i] ? this.wordClassVector[i] : other.wordClassVector[i];
        }
        for (int i = 0; i < w2vFeatureVector.length; i++) {
            w2vFeatureVector[i] = this.w2vFeatureVector[i] > other.w2vFeatureVector[i] ? this.w2vFeatureVector[i] : other.w2vFeatureVector[i];
        }
    }

    public void fill(double val) {
        for (int i = 0; i < surfaceVector.length; i++) {
            surfaceVector[i] = val;
        }
        for (int i = 0; i < sentimentVector.length; i++) {
            sentimentVector[i] = val;
        }
        for (int i = 0; i < wordClassVector.length; i++) {
            wordClassVector[i] = val;
        }
        for (int i = 0; i < w2vFeatureVector.length; i++) {
            w2vFeatureVector[i] = val;
        }
    }
}
