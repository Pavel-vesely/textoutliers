package detection;

import entities.ADSentenceBlock;
import entities.ADVector;
import entities.ADVector2;

import java.io.*;
import java.util.Arrays;

public class SlidingWindow {

    public static AnomalyTreeSet[] slidingWindow(String srcFilePath, String outVectorsFilePath, String outDistancesFilePath,
                                     int windowSize, ADSentenceBlock sumBlock, ADVector2 minVec, ADVector2 maxVec, int anomalyBuffer) throws IOException {
//        System.out.println(sumBlock.toCSVLine());
        int windowChars = 0;
        int goneChars = 0;
        ADSentenceBlock windowBlock = new ADSentenceBlock(-1, "");
        ADSentenceBlock[] window = new ADSentenceBlock[windowSize];
        int windowPointer;
        AnomalyTreeSet[] anomalyTreeSets = new AnomalyTreeSet[4];
        for (int i = 0; i < 4; i++) {
            anomalyTreeSets[i] = new AnomalyTreeSet();
            for (int j = 0; j < anomalyBuffer; j++) {
                anomalyTreeSets[i].add(new Anomaly(-1, -j, -j + 1, -1.0));
            }
        }

        //System.out.println(ADVector.getCSVHeader());

        try (
                BufferedReader br = new BufferedReader(new FileReader(srcFilePath));
                //BufferedWriter vectorBw = new BufferedWriter(new FileWriter(outVectorsFilePath));
                BufferedWriter distanceBw = new BufferedWriter(new FileWriter(outDistancesFilePath))
        ) {
            String line;
            for (windowPointer = 0; windowPointer < windowSize; windowPointer++) {
                if ((line = br.readLine()) != null) {
                    if (line.equals(ADSentenceBlock.getCSVHeader())) {
                        windowPointer--;
                        continue;
                    }
                    window[windowPointer] = new ADSentenceBlock(-1, "");
                    window[windowPointer].loadCSVLine(line);
                    windowChars += window[windowPointer].getChars();
                    windowBlock.increase(window[windowPointer]);
                    windowBlock.setStartChar(goneChars + 1);
                    windowBlock.setEndChar(goneChars + windowChars);
                    sumBlock.decrease(window[windowPointer]);
                } else {
                    throw new Exception();
                }
            }
            windowPointer = 0;
            windowBlock.setHeader(window[0].getHeader());
            windowBlock.setId(window[0].getId());

            //vectorBw.write(ADVector2.getCSVHeader());
            //vectorBw.newLine();
            //distanceBw.write(ADVector2.getCSVHeader() + ", distance");
            distanceBw.write(ADVector2.getShortHeader());
            distanceBw.newLine();

            String text = "";
//            for (int i = 0; i < windowSize; i++) {
//                text += window[(windowPointer + i) % windowSize].getFirstSentence();
//            }
//            windowBlock.setFirstSentence(text);

            ADVector2 windowVector = new ADVector2(windowBlock);
            ADVector2 sumVector = new ADVector2(sumBlock);
            windowVector.normalize(minVec, maxVec); //NORMING
            sumVector.normalize(minVec, maxVec);

            //vectorBw.write(windowVector.toCSVLine());
            //vectorBw.newLine();
            //distanceBw.write(windowVector.differenceToCSVLine(sumVector));
            distanceBw.write(windowVector.cosineDistancesToCSVLine(sumVector));
            distanceBw.newLine();


            anomalyTreeSets[0].updateSet(new Anomaly(windowBlock.getId(), windowBlock.getStartChar(), windowBlock.getEndChar(), windowVector.getCosineDistance(sumVector)));
            anomalyTreeSets[1].updateSet(new Anomaly(windowBlock.getId(), windowBlock.getStartChar(), windowBlock.getEndChar(), windowVector.getCosineDistanceNoW2V(sumVector)));
            anomalyTreeSets[2].updateSet(new Anomaly(windowBlock.getId(), windowBlock.getStartChar(), windowBlock.getEndChar(), windowVector.getCosineDistanceOnlyW2V(sumVector)));
            anomalyTreeSets[3].updateSet(new Anomaly(windowBlock.getId(), windowBlock.getStartChar(), windowBlock.getEndChar(), windowVector.getRandomDistance(sumVector)));
            while ((line = br.readLine()) != null) {
                windowChars -= window[windowPointer].getChars();
                windowBlock.decrease(window[windowPointer]);
                goneChars += window[windowPointer].getChars();
                sumBlock.increase(window[windowPointer]);

                window[windowPointer].loadCSVLine(line);
                windowBlock.setId(window[(windowPointer + 1) % windowSize].getId());
                windowBlock.setHeader(window[(windowPointer + 1) % windowSize].getHeader());
                windowChars += window[windowPointer].getChars();


                windowBlock.increase(window[windowPointer]);
                sumBlock.decrease(window[windowPointer]);

                windowBlock.setStartChar(goneChars + 1);
                windowBlock.setEndChar(goneChars + windowChars);

                windowPointer = (windowPointer + 1) % windowSize;
//                text = "";
//                for (int i = 0; i < windowSize; i++) {
//                    text += window[(windowPointer + i) % windowSize].getFirstSentence();
//                }
//                windowBlock.setFirstSentence(text);

                windowVector.loadSentenceBlock(windowBlock);
                sumVector.loadSentenceBlock(sumBlock);
                windowVector.normalize(minVec, maxVec); //normalize
                sumVector.normalize(minVec, maxVec);

//                vectorBw.write(windowVector.toCSVLine());
//                vectorBw.newLine();
                //distanceBw.write(windowVector.differenceToCSVLine(sumVector));
                distanceBw.write(windowVector.cosineDistancesToCSVLine(sumVector));
                distanceBw.newLine();
                anomalyTreeSets[0].updateSet(new Anomaly(windowBlock.getId(), windowBlock.getStartChar(), windowBlock.getEndChar(), windowVector.getCosineDistance(sumVector)));
                anomalyTreeSets[1].updateSet(new Anomaly(windowBlock.getId(), windowBlock.getStartChar(), windowBlock.getEndChar(), windowVector.getCosineDistanceNoW2V(sumVector)));
                anomalyTreeSets[2].updateSet(new Anomaly(windowBlock.getId(), windowBlock.getStartChar(), windowBlock.getEndChar(), windowVector.getCosineDistanceOnlyW2V(sumVector)));
                anomalyTreeSets[3].updateSet(new Anomaly(windowBlock.getId(), windowBlock.getStartChar(), windowBlock.getEndChar(), windowVector.getRandomDistance(sumVector)));
            }

            for (windowPointer = 0; windowPointer < windowSize; windowPointer++) {
                sumBlock.increase(window[windowPointer]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException();
        }
        return anomalyTreeSets;
    }

//    public static void slidingWindow(String srcFilePath, String outVectorsFilePath, int windowSize, ADSentenceBlock sumBlock, double[][] normVector) throws IOException {
//        //System.out.println(sumBlock.toCSVLine());
//        ADSentenceBlock windowBlock = new ADSentenceBlock(-1, "");
//        ADSentenceBlock[] window = new ADSentenceBlock[windowSize];
//        int windowPointer;
//
//        //System.out.println(ADVector2.getCSVHeader());
//
//        try (
//                BufferedReader br = new BufferedReader(new FileReader(srcFilePath));
//                BufferedWriter vectorBw = new BufferedWriter(new FileWriter(outVectorsFilePath))
//        ) {
//            String line;
//            for (windowPointer = 0; windowPointer < windowSize; windowPointer++) {
//                if ((line = br.readLine()) != null) {
//                    if (line.equals(ADSentenceBlock.getCSVHeader())) {
//                        windowPointer--;
//                        continue;
//                    }
//                    window[windowPointer] = new ADSentenceBlock(-1, "");
//                    window[windowPointer].loadCSVLine(line);
//                    windowBlock.increase(window[windowPointer]);
//                    sumBlock.decrease(window[windowPointer]);
//                } else {
//                    throw new Exception();
//                }
//            }
//            windowPointer = 0;
//            windowBlock.setHeader(window[0].getHeader());
//            windowBlock.setId(window[0].getId());
//
//            vectorBw.write(ADVector2.getCSVHeader());
//            vectorBw.newLine();
//
////            String text = "";
////            for (int i = 0; i < windowSize; i++) {
////                text += window[(windowPointer + i) % windowSize].getFirstSentence();
////            }
////            windowBlock.setFirstSentence(text);
//
//            ADVector2 windowVector = new ADVector2(windowBlock);
//            ADVector2 sumVector = new ADVector2(sumBlock);
//            windowVector.normalize(normVector); //normalize
//            sumVector.normalize(normVector);
//
//            vectorBw.write(windowVector.toCSVLine());
//            vectorBw.newLine();
//
//
//            while ((line = br.readLine()) != null) {
//                windowBlock.decrease(window[windowPointer]);
//                sumBlock.increase(window[windowPointer]);
//
//                window[windowPointer].loadCSVLine(line);
//                windowBlock.setId(window[(windowPointer + 1) % windowSize].getId());
//                windowBlock.setHeader(window[(windowPointer + 1) % windowSize].getHeader());
//
//                windowBlock.increase(window[windowPointer]);
//                sumBlock.decrease(window[windowPointer]);
//
//                windowPointer = (windowPointer + 1) % windowSize;
////                text = "";
////                for (int i = 0; i < windowSize; i++) {
////                    text += window[(windowPointer + i) % windowSize].getFirstSentence();
////                }
////                windowBlock.setFirstSentence(text);
//
//                windowVector.loadSentenceBlock(windowBlock);
//                sumVector.loadSentenceBlock(sumBlock);
//                windowVector.normalize(normVector); //normalize
//                sumVector.normalize(normVector);
//
//                vectorBw.write(windowVector.toCSVLine());
//                vectorBw.newLine();
//            }
//
//            for (windowPointer = 0; windowPointer < windowSize; windowPointer++) {
//                sumBlock.increase(window[windowPointer]);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new IOException();
//        }
//    }

    public static ADSentenceBlock getSum(String srcFilePath) {
        ADSentenceBlock sum = new ADSentenceBlock(0, "sum");
        ADSentenceBlock lineBlock = new ADSentenceBlock(-1, "");
        try (BufferedReader br = new BufferedReader(new FileReader(srcFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.equals(ADSentenceBlock.getCSVHeader())) {
                    continue;
                }
                lineBlock.loadCSVLine(line);
                sum.increase(lineBlock);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sum;
    }

    public static ADVector2[] getNormVectors(String srcFilePath) {
        ADSentenceBlock lineBlock = new ADSentenceBlock(-1, "");
        ADVector2 adVector = new ADVector2();
        ADVector2 minVector = new ADVector2();
        ADVector2 maxVector = new ADVector2();
        minVector.fill(Double.MAX_VALUE);
        maxVector.fill(Double.MIN_VALUE);
        double[] vector;
        try (BufferedReader br = new BufferedReader(new FileReader(srcFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.equals(ADSentenceBlock.getCSVHeader())) {
                    continue;
                }
                lineBlock.loadCSVLine(line);
                adVector.loadSentenceBlock(lineBlock);
                minVector.setLesser(adVector);
                maxVector.setGreater(adVector);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ADVector2[]{minVector, maxVector};
    }
}
