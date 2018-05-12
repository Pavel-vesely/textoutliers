package utils;

import java.io.IOException;

public class FrequencyTest {
    public static void main(String[] args) throws IOException {
        FrequencyMap map = new FrequencyMap(".\\resources\\wiki-word-freq.txt");
        System.out.println("The: " + Integer.toString(map.getFrequency("The")));
        System.out.println("these: " + Integer.toString(map.getFrequency("these")));
        System.out.println("it: " + Integer.toString(map.getFrequency("it")));
    }
}
