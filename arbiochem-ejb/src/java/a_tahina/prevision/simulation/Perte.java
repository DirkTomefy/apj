package a_tahina.prevision.simulation;

import java.time.LocalDate;

class Perte {

    final LocalDate date;
    final String compartiment, sexe;
    final double qte;

    Perte(LocalDate date, String compartiment, String sexe, double qte) {
        this.date = date;
        this.compartiment = compartiment;
        this.sexe = sexe;
        this.qte = qte;
    }
}
