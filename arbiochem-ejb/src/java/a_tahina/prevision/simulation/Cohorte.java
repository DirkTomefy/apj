package a_tahina.prevision.simulation;

import java.time.LocalDate;

import a_tahina.core.Sexe;

class Cohorte extends Stock {

    final LocalDate naissance;
    final String souche, compartiment;
    final Sexe sexe;
    final int generation;
    LocalDate reforme;

    Cohorte(LocalDate naissance, String souche, Sexe sexe, int generation, double pu, String compartiment, double qte) {
        super(pu, qte);
        this.naissance = naissance;
        this.souche = souche;
        this.sexe = sexe;
        this.generation = generation;
        this.compartiment = compartiment;
    }

    Cohorte copie() { return new Cohorte(naissance, souche, sexe, generation, pu, compartiment, qte); }

    boolean concerne(Perte p) {
        return qte > Elevage.EPS && p.compartiment.equals(compartiment) && p.sexe == sexe;
    }
}
