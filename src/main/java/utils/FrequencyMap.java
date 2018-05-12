package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public class FrequencyMap {
    private HashMap<String, Integer> hashMap;
    public FrequencyMap(String sourcePath) {
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

    public int getFrequency(String word) {
        return hashMap.get(word.toLowerCase());
    }
}
