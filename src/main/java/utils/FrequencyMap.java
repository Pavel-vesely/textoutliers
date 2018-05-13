package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public class FrequencyMap {
    private static HashMap<String, Integer> hashMap;
    public static void initialize(String sourcePath) {
        hashMap = new HashMap<String, Integer>();
        try (BufferedReader br = new BufferedReader(new FileReader(sourcePath))) {

            String line;
            double[] vector;
            Integer i = 0;
            String[] words;
            while ((line = br.readLine()) != null) {
                if (line.length() < 2) { continue; }
                if (line.contains("==")) { continue; }
                line = line.replace("[", "").replace("]", "");
                words = line.split(" ");
                for (String word : words) {
                    if (! hashMap.containsKey(word)) {
                        hashMap.put(word, i);
                        i++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getFrequency(String word) {
        if (!hashMap.containsKey(word.toLowerCase())) {
            return -1;
        }
        return hashMap.get(word.toLowerCase());
    }
}
