package a_tahina.core.calendrier;

import java.util.function.Predicate;

final class CalendrierStyle<T> {

    final String classe;
    final String legende;
    final Predicate<T> regle;

    CalendrierStyle(String classe, String legende, Predicate<T> regle) {
        this.classe = classe;
        this.legende = legende;
        this.regle = regle;
    }
}
