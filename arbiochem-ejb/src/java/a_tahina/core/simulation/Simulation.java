package a_tahina.core.simulation;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

public abstract class Simulation<J> {

    public Map<LocalDate, J> simuler() {
        Map<LocalDate, J> jours = new TreeMap<LocalDate, J>();
        preparer();
        LocalDate debut = debut();
        if (debut == null) return jours;
        LocalDate fin = fin();
        for (LocalDate d = debut; !d.isAfter(fin); d = d.plusDays(1)) {
            jours.put(d, simulerJour(d));
        }
        return jours;
    }

    protected abstract void preparer();

    protected abstract LocalDate debut();

    protected abstract LocalDate fin();

    protected abstract J simulerJour(LocalDate jour);
}
