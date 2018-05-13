package utils;

import java.io.IOException;

public class FrequencyTest {
    public static void main(String[] args) throws IOException {
        FrequencyMap.initialize(".\\resources\\wiki-word-freq.txt");
        System.out.println("The: " + Integer.toString(FrequencyMap.getFrequency("The")));
        System.out.println("these: " + Integer.toString(FrequencyMap.getFrequency("these")));
        System.out.println("it: " + Integer.toString(FrequencyMap.getFrequency("it")));
        System.out.println("it: " + Integer.toString(FrequencyMap.getFrequency("afagvagagag")));
    }
}
