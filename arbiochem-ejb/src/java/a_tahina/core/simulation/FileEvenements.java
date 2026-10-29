package a_tahina.core.simulation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class FileEvenements<T> {

    private final List<T> elements;
    private final Function<T, LocalDate> date;
    private int suivant;

    public FileEvenements(List<T> elements, Function<T, LocalDate> date) {
        this(elements, date, (a, b) -> 0);
    }

    public FileEvenements(List<T> elements, Function<T, LocalDate> date, Comparator<T> ordreDuJour) {
        this.elements = new ArrayList<T>(elements);
        this.date = date;
        Collections.sort(this.elements, Comparator.comparing(date).thenComparing(ordreDuJour));
    }

    public boolean estVide() {
        return elements.isEmpty();
    }

    public LocalDate premiereDate() {
        return estVide() ? null : date.apply(elements.get(0));
    }

    public LocalDate derniereDate() {
        return estVide() ? null : date.apply(elements.get(elements.size() - 1));
    }

    public List<T> jusquA(LocalDate jour) {
        List<T> dus = new ArrayList<T>();
        while (suivant < elements.size() && !date.apply(elements.get(suivant)).isAfter(jour)) {
            dus.add(elements.get(suivant++));
        }
        return dus;
    }
}
