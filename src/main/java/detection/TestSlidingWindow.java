package detection;

import entities.ADSentenceBlock;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;

public class TestSlidingWindow {
    public static final int windowSizes[] = {1, 2, 3, 5, 8, 10};
    private static final int bufferSize = 8338608; //8MB

    public static void testSlidingWindow(String normalFilePath, String anomalyFilePath, String outFilePath, double[][] normVector, int iterations) throws Exception {
        ADSentenceBlock complementBlock = SlidingWindow.getSum(normalFilePath);
        try (
            BufferedReader normalBr = new BufferedReader(new FileReader(normalFilePath), bufferSize);
            BufferedReader anomalyBr = new BufferedReader(new FileReader(anomalyFilePath), bufferSize);
            BufferedWriter outBw = new BufferedWriter(new FileWriter(outFilePath))
        ){
            normalBr.mark(bufferSize);
            anomalyBr.mark(bufferSize);
            int normalLines = 0;
            while (normalBr.readLine() != null) normalLines++;
            normalBr.reset();
            int anomalyLines = 0;
            while (normalBr.readLine() != null) normalLines++;
            anomalyBr.reset();

            //for (int j = 0; j < 2; j++) {
                //DO STUFF
                int windowSize = 10;
                ADSentenceBlock windowBlock = new ADSentenceBlock(0, "window");
                ADSentenceBlock window[] = new ADSentenceBlock[windowSize];
                String line;
                int windowPointer;
                for (int iter = 0; iter < iterations; iter++) {
                    for (windowPointer = 0; windowPointer < windowSize; windowPointer++) {
                        if ((line = normalBr.readLine()) != null) {
                            if (line.equals(ADSentenceBlock.getCSVHeader())) {
                                windowPointer--;
                                continue;
                            }
                            window[windowPointer] = new ADSentenceBlock(-1, "");
                            window[windowPointer].loadCSVLine(line);
                            windowBlock.increase(window[windowPointer]);
                            complementBlock.decrease(window[windowPointer]);
                        } else {
                            throw new Exception();
                        }
                    }

                    Random rand = new Random();
                    int normalMargin = rand.nextInt(normalLines - 2 * windowSize);
                    int anomalyMargin = rand.nextInt(anomalyLines - windowSize);


                }
                //swap normal and anomaly file
            //}
        }

    }
}
