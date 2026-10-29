package a_tahina.prevision.simulation;

import java.time.LocalDate;

import a_tahina.core.Sexe;

class Perte {

    final LocalDate date;
    final String compartiment;
    final Sexe sexe;
    final double qte;

    Perte(LocalDate date, String compartiment, Sexe sexe, double qte) {
        this.date = date;
        this.compartiment = compartiment;
        this.sexe = sexe;
        this.qte = qte;
    }
}
