package a_tahina.prevision.simulation;

class ParametreSouche {

    final int nbJoursEclosion, semaineDebutPonte, dureeVieSemaines;
    final double tauxEclosion, ratioFemelle;

    ParametreSouche(int nbJoursEclosion, int semaineDebutPonte, int dureeVieSemaines, double tauxEclosion, double ratioFemelle) {
        this.nbJoursEclosion = nbJoursEclosion;
        this.semaineDebutPonte = semaineDebutPonte;
        this.dureeVieSemaines = dureeVieSemaines;
        this.tauxEclosion = tauxEclosion;
        this.ratioFemelle = ratioFemelle;
    }
}
