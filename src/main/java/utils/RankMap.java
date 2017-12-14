package utils;

import java.util.Map;
import java.util.TreeMap;

public class RankMap {
    public static Map getRankMap(Map<String, Integer> frequencyMap, Integer maxSize) {
        TreeMap<Integer, String> revMap = new TreeMap<>();
        frequencyMap.forEach((type, amount) -> {
            if (revMap.containsKey(amount)) {
                revMap.replace(amount, revMap.get(amount) + "#" + type);
            } else {
                revMap.put(amount, type);
            }
        });
        TreeMap<String, Integer> rankMap = new TreeMap<>();
        String[] brokenLine;
        int i = 0;
        for (Integer num: revMap.descendingKeySet()) {
            brokenLine = revMap.get(num).split("#");
            for (String dep : brokenLine) {
                if (maxSize == null || rankMap.size() < maxSize) {
                    rankMap.put(dep, i);
                    i++;
                }
            }
        }
        return rankMap;
    }
}
