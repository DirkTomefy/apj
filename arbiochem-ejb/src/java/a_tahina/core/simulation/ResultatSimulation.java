package a_tahina.core.simulation;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class ResultatSimulation<J> {

    private final Map<LocalDate, J> jours = new TreeMap<LocalDate, J>();
    private LocalDate limite;

    void ajouterJour(LocalDate date, J jour) {
        jours.put(date, jour);
    }

    void limiter(LocalDate limite) {
        this.limite = limite;
    }

    public Map<LocalDate, J> getJours() { return Collections.unmodifiableMap(jours); }

    public J jour(LocalDate date) { return jours.get(date); }

    public LocalDate getLimite() { return limite; }

    public boolean couvre(LocalDate date) {
        return limite == null || !date.isAfter(limite);
    }
}
