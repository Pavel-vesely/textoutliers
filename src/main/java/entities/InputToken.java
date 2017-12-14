package entities;

public class InputToken {
    private String word = "";
    private String lemma = "";
    private Integer characters = 0;
    private Integer syllables = 0;
    private String POS = "";

    public InputToken(){}

    public InputToken(String word, String lemma, Integer characters, Integer syllables, String POS){
        this.word = word;
        this.lemma = lemma;
        this.characters = characters;
        this.syllables = syllables;
        this.POS = POS;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public Integer getCharacters() {
        return characters;
    }

    public void setCharacters(Integer characters) {
        this.characters = characters;
    }

    public Integer getSyllables() {
        return syllables;
    }

    public void setSyllables(Integer syllables) {
        this.syllables = syllables;
    }

    public String getPOS() {
        return POS;
    }

    public void setPOS(String POS) {
        this.POS = POS;
    }

    public String toString() {
        return "{\"InputToken\": {" +
                "\"word\": \"" + word + "\"," +
                "\"lemma\": \"" + lemma + "\"," +
                "\"characters\": " + characters.toString() + "," +
                "\"syllables\": " + syllables.toString() + "," +
                "\"POS\": \"" + POS + "\"" +
                "}}";
    }

    public String toJSON() {
        return this.toString();
    }
}

