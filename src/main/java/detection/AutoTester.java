package detection;

import entities.ADSentenceBlock;
import entities.ADVector2;
import entities.PosTags;

import java.io.*;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

public class AutoTester {
        public static void main(String[] args) throws IOException {
            File normalFile = new File(".\\resources\\test2\\c1mini-articles.xml-sblock.csv");
            File anomalyFile = new File(".\\resources\\test2\\c2micro-anarchist_cookbook.xml-sblock.csv");
            File testFile = new File(".\\resources\\test2\\c1-sblock.csv");

            BufferedReader normReader = new BufferedReader(new FileReader(normalFile));
            int normLines = 0;
            while (normReader.readLine() != null) normLines++;
            normReader.close();
            BufferedReader anoReader = new BufferedReader(new FileReader(anomalyFile));
            int anoLines = 0;
            while (anoReader.readLine() != null) anoLines++;
            anoReader.close();
            Random rnd = new Random();
            int normOffset;
            int anoOffset;
            int insideOffset;
            BufferedWriter writer;
            String line;
            int anoStart;
            int anoSize;
            int tempSize;

            int[][] results = new int[4][20];
            for (int[] res : results) {
                Arrays.fill(res, 0);
            }

            for (int x = 0; x < 500; x++) {


            normOffset = rnd.nextInt(normLines - 501);
            anoOffset = rnd.nextInt(anoLines - 20) + 1;
            insideOffset = rnd.nextInt(500);

            normReader = new BufferedReader(new FileReader(normalFile));
            anoReader = new BufferedReader(new FileReader(anomalyFile));
            writer = new BufferedWriter(new FileWriter(testFile));

            writer.write(normReader.readLine());
            writer.newLine();
            for (int i = 0; i < normOffset; i++) {
                normReader.readLine();
            }
            for (int i = 0; i < anoOffset; i++) {
                anoReader.readLine();
            }
            anoStart = 0;
            tempSize = 0;
            anoSize = 0;
            for (int i = 0; i < 200; i++) {
                if (i == insideOffset) {
                    anoStart = tempSize;
                    for (int j = 0; j < 10; j++) {
                        line = anoReader.readLine();
                        writer.write(line);
                        writer.newLine();
                        anoSize += Integer.parseInt(line.split(",")[6].replace(" ", ""));
                    }
                }
                line = normReader.readLine();
                writer.write(line);
                writer.newLine();
                tempSize += Integer.parseInt(line.split(",")[6].replace(" ", ""));
            }
            normReader.close();
            anoReader.close();
            writer.close();
//            System.out.println("Anomaly " + Integer.toString(x) + ": " + Integer.toString(anoStart) + "-" + Integer.toString(anoStart + anoSize));
//            System.out.println(anoStart + (anoSize / 2));

            ADVector2[] normVectors = SlidingWindow.getNormVectors(testFile.getPath());
            ADSentenceBlock sumBlock = SlidingWindow.getSum(testFile.getPath());

            AnomalyTreeSet[] sets = SlidingWindow.slidingWindow(testFile.getPath(), "", testFile.getPath() + "-dist", 10, sumBlock, normVectors[0], normVectors[1], 20);
            Anomaly anomaly;
            for (int i = 0; i < 4; i++) {
                Iterator iterator;
                iterator = sets[i].descendingIterator();

                // displaying the Tree set data
//                System.out.println("Tree set data in descending order: " + Integer.toString(i));
                int j = 0;
                while (iterator.hasNext()) {
                    anomaly = (Anomaly) iterator.next();
//                    System.out.print(Integer.toString(j) + " ");
//                    System.out.print(anomaly);
//                    System.out.println();
                    if ((((anoStart + anoSize) / 2) > anomaly.getStartChar()) && ((anoStart + anoSize) / 2) < anomaly.getEndChar()) {
                        results[i][j]++;
                        break;
                    }
                    j++;
                }
            }
        }
            for (int[] res : results) {
                for (int i = 0; i < 20; i++) {
                    System.out.print(Integer.toString(res[i]) + ", ");
                }
                System.out.println();
            }
        }

}
