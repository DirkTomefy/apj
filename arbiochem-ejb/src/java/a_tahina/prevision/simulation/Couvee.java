package a_tahina.prevision.simulation;

import java.time.LocalDate;

class Couvee extends Stock {

    final LocalDate eclosion;
    final String souche, compartiment;
    final int generation;

    Couvee(LocalDate eclosion, String souche, String compartiment, int generation, double pu) {
        super(pu, 0);
        this.eclosion = eclosion;
        this.souche = souche;
        this.compartiment = compartiment;
        this.generation = generation;
    }
}
