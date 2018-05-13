package utils;

public class NerTags {
    public static int nerTagToInt(String nerTag) {
        switch (nerTag.toUpperCase()) {
            case "O":
                return -1;
            case "PERSON": return 0;
            case "LOCATION": return 1;
            case "ORGANIZATION": return 2;
            case "MONEY":
            case "NUMBER":
            case "ORDINAL":
            case "PERCENT":
                return 3;
            case "DATE":
            case "TIME":
            case "DURATION":
            case "SET":
                return 4;
            default:
                return 5;
        }
    }
}
