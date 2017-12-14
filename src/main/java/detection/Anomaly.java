package detection;

public class Anomaly implements Comparable<Anomaly> {
    private double distance;
    private int startChar;
    private int endChar;
    private int id;

    public Anomaly(int id, int startChar, int endChar, double distance) {
        this.id = id;
        this.startChar = startChar;
        this.endChar = endChar;
        this.distance = distance;
    }

    public int getId() {
        return id;
    }

    public int getStartChar() {
        return startChar;
    }

    public int getEndChar() {
        return endChar;
    }

    public double getDistance() {
        return distance;
    }

    public boolean overlaps(Anomaly other) {
        if (startChar < other.startChar) {
            if (other.startChar < endChar) {
                return true;
            }
        } else {
            if (startChar < other.endChar) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int compareTo(Anomaly other) {
        int result = Double.compare(this.distance, other.distance);
        if (result == 0) {
            return Integer.compare(this.startChar, other.startChar);
        }
        return result;
    }

    @Override
    public String toString() {
        return "{\"Anomaly\": {" +
                "\"id\": " + Integer.toString(id) + "," +
                "\"startChar\": " + Integer.toString(startChar) + "," +
                "\"endChar\": " + Integer.toString(endChar) + "," +
                "\"distance\": " + Double.toString(distance) +
                "}}";
    }
}
