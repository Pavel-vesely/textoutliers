package detection;

import entities.ADSentenceBlock;
import entities.ADVector2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

public class Detection {
    public static void main(String[] args) throws IOException {
        ArrayList<String> filePaths =  new ArrayList<>();
        Files.newDirectoryStream(Paths.get(".\\resources\\out"),
                path -> path.toString().endsWith("-sblock.csv"))
                .forEach(path -> filePaths.add(path.toString()));
        String fileName;
        AnomalyTreeSet[] sets;
        for (String filePath : filePaths) {
            fileName = filePath.substring(0, filePath.length() - 10);
            int windowSize = 10;

            String vecPath = fileName + "vec" + Integer.toString(windowSize) + ".csv";
            String distPath = fileName + "dist" + Integer.toString(windowSize) + ".csv";

            ADVector2[] normVectors = SlidingWindow.getNormVectors(filePath);
            ADSentenceBlock sumBlock = SlidingWindow.getSum(filePath);

            sets = SlidingWindow.slidingWindow(filePath, vecPath, distPath, windowSize, sumBlock, normVectors[0], normVectors[1], 20);
            for (int i = 0; i < 4; i++) {
                Iterator iterator;
                iterator = sets[i].descendingIterator();

                // displaying the Tree set data
                System.out.println("Tree set data in descending order: " + Integer.toString(i));

                while (iterator.hasNext()) {
                    System.out.println(iterator.next() + " ");
                }
            }
        }
    }
}
