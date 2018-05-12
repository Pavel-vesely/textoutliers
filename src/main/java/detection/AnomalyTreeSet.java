package detection;

import java.util.TreeSet;

public class AnomalyTreeSet extends TreeSet<Anomaly> {

    public void updateSet(Anomaly item) {
        if (item.getDistance() < this.first().getDistance()) {
            return;
        }
        Anomaly overlapping = null;
        for (Anomaly setItem : this) {
            if (item.overlaps(setItem)) {
                overlapping = setItem;
                break;
            }
        }
        if (overlapping != null) {
            if(overlapping.getDistance() < item.getDistance()) {
                this.remove(overlapping);
                this.add(item);
                return;
            }
        } else {
            this.remove(this.first());
            this.add(item);
        }
    }
}
