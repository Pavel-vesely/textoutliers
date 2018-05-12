package detection;
import entities.ADSentenceBlock;
import utils.Constants;
import utils.W2vVectorOperations;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.concurrent.Callable;

public class W2VSelect {
    public static void main(String[] args){
        testMe();
    }

    public static void testMe() {
        double testArr[] = new double[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};
        int top[] = utils.W2vVectorOperations.getTopIndexes(testArr,3);
        Arrays.sort(top);
        for (int x : top){
            System.out.println(x);
        }

    }

    public static int[] selectW2VIndices(String srcFilePath, int n, String mode) {
        ADSentenceBlock lineBlock = new ADSentenceBlock(-1, "");
        double[] w2vAggregate = new double[Constants.W2V_VECTOR_LEN];
        try (BufferedReader br = new BufferedReader(new FileReader(srcFilePath))) {
            String line;
            double[] vector;
            while ((line = br.readLine()) != null) {
                if (line.equals(ADSentenceBlock.getCSVHeader())) {
                    continue;
                }
                lineBlock.loadCSVLine(line);
                vector = lineBlock.getW2vArray();
                vector = utils.W2vVectorOperations.divideByDouble(vector, (double)lineBlock.getWords());
                switch (mode) {
                    case "sum":
                        w2vAggregate = utils.W2vVectorOperations.addDoubleVectors(w2vAggregate, vector);
                        break;
                    case "sum-abs":
                        w2vAggregate = utils.W2vVectorOperations.addAbsDoubleVectors(w2vAggregate, vector);
                        break;
                    case "sum-squared":
                        w2vAggregate = utils.W2vVectorOperations.addSquareDoubleVectors(w2vAggregate, vector);
                        break;
                        default:
                            throw new IllegalArgumentException();
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        int top[] = utils.W2vVectorOperations.getTopIndexes(w2vAggregate,n);
        Arrays.sort(top);
        return top;
    }

    public static int[] selectW2VIndicesIDF(String srcFilePath, String allFilePath, int n, String mode) {
        ADSentenceBlock lineBlock = new ADSentenceBlock(-1, "");
        double[] w2vAggregate = new double[Constants.W2V_VECTOR_LEN];
        double[] w2vAllAggregate = new double[Constants.W2V_VECTOR_LEN];
        try (BufferedReader br = new BufferedReader(new FileReader(srcFilePath))) {
            String line;
            double[] vector;
            while ((line = br.readLine()) != null) {
                if (line.equals(ADSentenceBlock.getCSVHeader())) {
                    continue;
                }
                lineBlock.loadCSVLine(line);
                vector = lineBlock.getW2vArray();
                vector = utils.W2vVectorOperations.divideByDouble(vector, (double)lineBlock.getWords());
                switch (mode) {
                    case "sum":
                        w2vAggregate = utils.W2vVectorOperations.addDoubleVectors(w2vAggregate, vector);
                        break;
                    case "sum-abs":
                        w2vAggregate = utils.W2vVectorOperations.addAbsDoubleVectors(w2vAggregate, vector);
                        break;
                    case "sum-squared":
                        w2vAggregate = utils.W2vVectorOperations.addSquareDoubleVectors(w2vAggregate, vector);
                        break;
                    default:
                        throw new IllegalArgumentException();
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (BufferedReader br = new BufferedReader(new FileReader(allFilePath))) {
            String line;
            double[] vector;
            while ((line = br.readLine()) != null) {
                if (line.equals(ADSentenceBlock.getCSVHeader())) {
                    continue;
                }
                lineBlock.loadCSVLine(line);
                vector = lineBlock.getW2vArray();
                vector = utils.W2vVectorOperations.divideByDouble(vector, (double)lineBlock.getWords());
                switch (mode) {
                    case "sum":
                        w2vAllAggregate = utils.W2vVectorOperations.addDoubleVectors(w2vAggregate, vector);
                        break;
                    case "sum-abs":
                        w2vAllAggregate = utils.W2vVectorOperations.addAbsDoubleVectors(w2vAggregate, vector);
                        break;
                    case "sum-squared":
                        w2vAllAggregate = utils.W2vVectorOperations.addSquareDoubleVectors(w2vAggregate, vector);
                        break;
                    default:
                        throw new IllegalArgumentException();
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        w2vAggregate = utils.W2vVectorOperations.divideByVector(w2vAggregate, w2vAllAggregate);
        int top[] = utils.W2vVectorOperations.getTopIndexes(w2vAggregate,n);
        Arrays.sort(top);
        return top;
    }

//    public static int[] selectW2VSumAbsIndices(String srcFilePath, int n) {
//        ADSentenceBlock lineBlock = new ADSentenceBlock(-1, "");
//        double[] w2vAggregate = new double[Constants.W2V_VECTOR_LEN];
//        try (BufferedReader br = new BufferedReader(new FileReader(srcFilePath))) {
//            String line;
//            double[] vector;
//            while ((line = br.readLine()) != null) {
//                if (line.equals(ADSentenceBlock.getCSVHeader())) {
//                    continue;
//                }
//                lineBlock.loadCSVLine(line);
//                vector = lineBlock.getW2vArray();
//
//                w2vAggregate = utils.W2vVectorOperations.addAbsDoubleVectors(w2vAggregate, vector);
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        int top[] = utils.W2vVectorOperations.getTopIndexes(w2vAggregate,n);
//        Arrays.sort(top);
//        return top;
//    }
//
//    public static int[] selectW2VSumSquareIndices(String srcFilePath, int n) {
//        ADSentenceBlock lineBlock = new ADSentenceBlock(-1, "");
//        double[] w2vAggregate = new double[Constants.W2V_VECTOR_LEN];
//        try (BufferedReader br = new BufferedReader(new FileReader(srcFilePath))) {
//            String line;
//            double[] vector;
//            while ((line = br.readLine()) != null) {
//                if (line.equals(ADSentenceBlock.getCSVHeader())) {
//                    continue;
//                }
//                lineBlock.loadCSVLine(line);
//                vector = lineBlock.getW2vArray();
//
//                w2vAggregate = utils.W2vVectorOperations.addSquareDoubleVectors(w2vAggregate, vector);
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        int top[] = utils.W2vVectorOperations.getTopIndexes(w2vAggregate,n);
//        Arrays.sort(top);
//        return top;
//    }

}
