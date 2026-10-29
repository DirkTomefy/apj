package a_tahina.prevision.simulation;

import java.time.LocalDate;

class Cohorte {

    final LocalDate naissance;
    final String souche, sexe, compartiment;
    final int generation;
    final double pu;
    double qte;

    Cohorte(LocalDate naissance, String souche, String sexe, int generation, double pu, String compartiment, double qte) {
        this.naissance = naissance;
        this.souche = souche;
        this.sexe = sexe;
        this.generation = generation;
        this.pu = pu;
        this.compartiment = compartiment;
        this.qte = qte;
    }

    Cohorte copie() { return new Cohorte(naissance, souche, sexe, generation, pu, compartiment, qte); }

    boolean concerne(Perte p) {
        return qte > Elevage.EPS && p.compartiment.equals(compartiment) && p.sexe.equals(sexe);
    }
}
