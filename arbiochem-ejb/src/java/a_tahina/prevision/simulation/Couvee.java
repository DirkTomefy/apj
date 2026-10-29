package a_tahina.prevision.simulation;

import java.time.LocalDate;

class Couvee {

    final LocalDate eclosion;
    final String souche;
    final int generation;
    final double pu;
    double qte;

    Couvee(LocalDate eclosion, String souche, int generation, double pu) {
        this.eclosion = eclosion;
        this.souche = souche;
        this.generation = generation;
        this.pu = pu;
    }
}
