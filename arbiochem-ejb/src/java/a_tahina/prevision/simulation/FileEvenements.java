package a_tahina.prevision.simulation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

class FileEvenements<T> {

    private final List<T> elements;
    private final Function<T, LocalDate> date;
    private int suivant;

    FileEvenements(List<T> elements, Function<T, LocalDate> date) {
        this(elements, date, (a, b) -> 0);
    }

    FileEvenements(List<T> elements, Function<T, LocalDate> date, Comparator<T> ordreDuJour) {
        this.elements = new ArrayList<T>(elements);
        this.date = date;
        Collections.sort(this.elements, Comparator.comparing(date).thenComparing(ordreDuJour));
    }

    LocalDate premiereDate() {
        return elements.isEmpty() ? null : date.apply(elements.get(0));
    }

    LocalDate derniereDate() {
        return elements.isEmpty() ? null : date.apply(elements.get(elements.size() - 1));
    }

    List<T> jusquA(LocalDate jour) {
        List<T> dus = new ArrayList<T>();
        while (suivant < elements.size() && !date.apply(elements.get(suivant)).isAfter(jour)) {
            dus.add(elements.get(suivant++));
        }
        return dus;
    }
}
