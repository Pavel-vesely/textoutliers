package utils;

import org.nd4j.linalg.api.ndarray.INDArray;

public class W2vVectorOperations {
    private W2vVectorOperations() {}

    public static double[] wordMatrixToDoubles(INDArray wordMatrix) throws NullPointerException {
        if(wordMatrix == null) {
            throw new NullPointerException();
        }
        double[] vector = new double[Constants.W2V_VECTOR_LEN];
        for (int i = 0; i < Constants.W2V_VECTOR_LEN; i++) {
            vector[i] = wordMatrix.getDouble(i);
        }
        return vector;
    }

    public static double[] addDoubleVectors(double[] vec1, double[] vec2) throws IllegalArgumentException {
        if (vec1.length != vec2.length) throw new IllegalArgumentException();

        for(int i = 0; i < vec1.length; i++) {
            vec1[i] += vec2[i];
        }
        return vec1;
    }

    public static double[] divideByDouble(double[] vec1, double divider) {
        for(int i = 0; i < vec1.length; i++) {
            vec1[i] = vec1[i] / divider;
        }
        return vec1;
    }

    public static double[] divideByVector(double[] vec1, double[] dividerVec) throws IllegalArgumentException {
        if (vec1.length != dividerVec.length) throw new IllegalArgumentException();

        for(int i = 0; i < vec1.length; i++) {
            vec1[i] = vec1[i] / dividerVec[i];
        }
        return vec1;
    }

    public static double[] addAbsDoubleVectors(double[] vec1, double[] vec2) throws IllegalArgumentException {
        if (vec1.length != vec2.length) throw new IllegalArgumentException();

        for(int i = 0; i < vec1.length; i++) {
            vec1[i] += Math.abs(vec2[i]);
        }
        return vec1;
    }

    public static double[] addSquareDoubleVectors(double[] vec1, double[] vec2) throws IllegalArgumentException {
        if (vec1.length != vec2.length) throw new IllegalArgumentException();

        for(int i = 0; i < vec1.length; i++) {
            vec1[i] += (vec2[i] * vec2[i]);
        }
        return vec1;
    }

    public static int[] getTopIndexes(double[] vec, int n) {
        double[] copyVec = new double[vec.length];
        for (int i = 0; i < vec.length; i++) {
            copyVec[i] = vec[i];
        }
        int[] maxIndexes = new int[n];
        int maxInx;
        double maxVal;
        for (int i = 0; i < n; i++) {
            maxInx = -1;
            maxVal = Double.MIN_VALUE;
            for (int j = 0; j < copyVec.length; j++) {
                if (maxVal < copyVec[j]) {
                    maxVal = copyVec[j];
                    maxInx = j;
                }
            }
            maxIndexes[i] = maxInx;
            copyVec[maxInx] = Double.MIN_VALUE;
        }
        return maxIndexes;
    }

    public static double[] getArrayByIndexes(INDArray wordMatrix, int[] indexes) {
        double[] vec = wordMatrixToDoubles(wordMatrix);
        double[] out = new double[indexes.length];
        for (int i = 0; i < indexes.length; i ++) {
            out[i] = vec[indexes[i]];
        }
        return out;
    }
}
