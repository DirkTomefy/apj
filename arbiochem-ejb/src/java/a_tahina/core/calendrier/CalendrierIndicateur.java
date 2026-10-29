package a_tahina.core.calendrier;

import java.util.function.ToDoubleFunction;

final class CalendrierIndicateur<T> {

    final String libelle;
    final ToDoubleFunction<T> valeur;
    final boolean signe;

    CalendrierIndicateur(String libelle, ToDoubleFunction<T> valeur, boolean signe) {
        this.libelle = libelle;
        this.valeur = valeur;
        this.signe = signe;
    }
}
